package com.newmaa.othtech.common.dialogue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Machine-local credential protection. Keep the vault directory private and back up neither file together
 * unless the backup is separately protected: anyone who can read both files (including root/administrators)
 * can recover the key. Filesystems without verifiable owner-only ACLs/POSIX modes or atomic moves fail closed.
 */
public final class AyanamiKeyVault {

    private static final String MASTER = "master.bin";
    private static final String DATA = "credential.gcm";
    private static final String LOCK = "write.lock";
    private static final byte[] MAGIC = { 'A', 'Y', 'K', 1 };
    private static final int MASTER_BYTES = 16; // AES-128 is available even on restricted Java 8 installations.
    private static final int ENDPOINT_BYTES = 32;
    private static final int NONCE_BYTES = 12;
    private static final int TAG_BYTES = 16;
    private static final int HEADER_BYTES = MAGIC.length + ENDPOINT_BYTES + NONCE_BYTES;
    private static final int MAX_KEY = 4096;
    private static final int MAX_RECORD = HEADER_BYTES + MAX_KEY + TAG_BYTES;
    private static final Object JVM_LOCK = new Object();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<PosixFilePermission> PRIVATE_MODE = Collections
        .unmodifiableSet(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));

    private final Path directory;

    public AyanamiKeyVault(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory")
            .toAbsolutePath()
            .normalize();
    }

    /** Returns null only for no saved credential, or a valid credential bound to another endpoint. */
    public String read(String endpoint) throws IOException {
        byte[] expected = digestEndpoint(endpoint);
        synchronized (JVM_LOCK) {
            if (!directoryExists()) return null;
            byte[] record = optionalRead(directory.resolve(DATA), MAX_RECORD);
            if (record == null) return null;
            byte[] master = requiredMaster();
            try {
                return open(record, master, expected);
            } finally {
                Arrays.fill(master, (byte) 0);
            }
        }
    }

    /** Replaces the single saved credential. Neither this method nor the file format exposes its plaintext. */
    public void save(String endpoint, String apiKey) throws IOException {
        byte[] binding = digestEndpoint(endpoint);
        validateKey(apiKey);
        byte[] plaintext = apiKey.getBytes(StandardCharsets.US_ASCII);
        try {
            synchronized (JVM_LOCK) {
                if (!directoryExists()) Files.createDirectories(directory);
                if (!directoryExists()) throw new IOException("Credential vault directory is unavailable.");
                Path lock = directory.resolve(LOCK);
                // The lock contains no data. CREATE does not truncate, so concurrent processes lock the same file.
                try (
                    FileChannel channel = FileChannel
                        .open(lock, StandardOpenOption.CREATE, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS);
                    FileLock held = channel.tryLock()) {
                    if (held == null) throw new IOException("Credential vault is busy.");
                    byte[] prior = optionalRead(directory.resolve(DATA), MAX_RECORD);
                    byte[] master = optionalRead(directory.resolve(MASTER), MASTER_BYTES);
                    if (master == null && prior != null) throw new IOException("Credential vault master is missing.");
                    if (master == null) {
                        master = new byte[MASTER_BYTES];
                        RANDOM.nextBytes(master);
                        try {
                            atomicWrite(directory.resolve(MASTER), master);
                        } catch (IOException | RuntimeException e) {
                            Arrays.fill(master, (byte) 0);
                            throw e;
                        }
                    }
                    try {
                        if (master.length != MASTER_BYTES) throw new IOException("Invalid credential vault master.");
                        if (prior != null) open(prior, master, null); // Never silently overwrite damaged records.
                        atomicWrite(directory.resolve(DATA), seal(plaintext, master, binding));
                    } finally {
                        Arrays.fill(master, (byte) 0);
                    }
                }
            }
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    private boolean directoryExists() throws IOException {
        try {
            if (!Files.readAttributes(directory, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                .isDirectory()) {
                throw new IOException("Invalid credential vault directory.");
            }
            return true;
        } catch (NoSuchFileException e) {
            return false;
        }
    }

    private byte[] requiredMaster() throws IOException {
        byte[] master = optionalRead(directory.resolve(MASTER), MASTER_BYTES);
        if (master == null) throw new IOException("Credential vault master is missing.");
        if (master.length != MASTER_BYTES) {
            Arrays.fill(master, (byte) 0);
            throw new IOException("Invalid credential vault master.");
        }
        return master;
    }

    private static byte[] digestEndpoint(String endpoint) throws IOException {
        if (endpoint == null || endpoint.length() > 2048) throw new IOException("Invalid credential endpoint.");
        try {
            URI uri = new URI(endpoint.trim());
            String host = uri.getHost();
            String scheme = uri.getScheme();
            if (host == null || scheme == null
                || uri.getRawUserInfo() != null
                || uri.getRawFragment() != null
                || !(scheme.equalsIgnoreCase("https") || scheme.equalsIgnoreCase("http"))) {
                throw new IOException("Invalid credential endpoint.");
            }
            String authority = uri.getRawAuthority();
            // Fold only scheme/host case. Bind the exact raw route/query and preserve explicit port syntax.
            String canonical = scheme.toLowerCase(Locale.ROOT) + "://"
                + host.toLowerCase(Locale.ROOT)
                + authority.substring(host.length())
                + uri.getRawPath()
                + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            return MessageDigest.getInstance("SHA-256")
                .digest(canonical.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException | URISyntaxException e) {
            throw new IOException("Invalid credential endpoint.");
        }
    }

    private static void validateKey(String key) throws IOException {
        if (key == null || key.isEmpty() || key.length() > MAX_KEY) throw new IOException("Invalid API key.");
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) < '!' || key.charAt(i) > '~') throw new IOException("Invalid API key.");
        }
    }

    private static byte[] seal(byte[] plaintext, byte[] master, byte[] binding) throws IOException {
        byte[] nonce = new byte[NONCE_BYTES];
        RANDOM.nextBytes(nonce);
        byte[] header = ByteBuffer.allocate(HEADER_BYTES)
            .put(MAGIC)
            .put(binding)
            .put(nonce)
            .array();
        try {
            Cipher cipher = cipher(Cipher.ENCRYPT_MODE, master, nonce);
            cipher.updateAAD(header);
            byte[] encrypted = cipher.doFinal(plaintext);
            return ByteBuffer.allocate(header.length + encrypted.length)
                .put(header)
                .put(encrypted)
                .array();
        } catch (GeneralSecurityException e) {
            throw new IOException("Credential vault encryption failed.");
        }
    }

    private static String open(byte[] record, byte[] master, byte[] expected) throws IOException {
        if (record.length < HEADER_BYTES + TAG_BYTES + 1
            || !Arrays.equals(MAGIC, Arrays.copyOfRange(record, 0, MAGIC.length))) {
            throw new IOException("Invalid credential vault record.");
        }
        byte[] nonce = Arrays.copyOfRange(record, MAGIC.length + ENDPOINT_BYTES, HEADER_BYTES);
        byte[] plaintext = null;
        try {
            Cipher cipher = cipher(Cipher.DECRYPT_MODE, master, nonce);
            cipher.updateAAD(record, 0, HEADER_BYTES);
            // Authenticate the stored binding before testing it for equality, so header tampering cannot look like a
            // mismatch.
            plaintext = cipher.doFinal(record, HEADER_BYTES, record.length - HEADER_BYTES);
            if (expected == null || !MessageDigest
                .isEqual(expected, Arrays.copyOfRange(record, MAGIC.length, MAGIC.length + ENDPOINT_BYTES)))
                return null;
            String key = new String(plaintext, StandardCharsets.US_ASCII);
            validateKey(key);
            return key;
        } catch (GeneralSecurityException e) {
            throw new IOException("Credential vault authentication failed.");
        } finally {
            if (plaintext != null) Arrays.fill(plaintext, (byte) 0);
        }
    }

    private static Cipher cipher(int mode, byte[] master, byte[] nonce) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, new SecretKeySpec(master, "AES"), new GCMParameterSpec(TAG_BYTES * 8, nonce));
        return cipher;
    }

    private static byte[] optionalRead(Path path, int maximum) throws IOException {
        try {
            verifyProtected(path);
            try (FileChannel input = FileChannel.open(path, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS)) {
                if (input.size() > maximum) throw new IOException("Credential vault file exceeds its size limit.");
                ByteBuffer bytes = ByteBuffer.allocate(maximum + 1);
                while (bytes.hasRemaining() && input.read(bytes) != -1) { /* read up to the cap */ }
                if (!bytes.hasRemaining()) throw new IOException("Credential vault file exceeds its size limit.");
                return Arrays.copyOf(bytes.array(), bytes.position());
            }
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private void atomicWrite(Path destination, byte[] contents) throws IOException {
        Path temp = protectedTemp();
        try {
            try (FileChannel file = FileChannel.open(temp, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
                ByteBuffer bytes = ByteBuffer.wrap(contents);
                while (bytes.hasRemaining()) file.write(bytes);
                file.force(true);
            }
            Files.move(temp, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private Path protectedTemp() throws IOException {
        if (Files.getFileStore(directory)
            .supportsFileAttributeView(PosixFileAttributeView.class)) {
            return Files
                .createTempFile(directory, ".vault-", ".tmp", PosixFilePermissions.asFileAttribute(PRIVATE_MODE));
        }
        if (!Files.getFileStore(directory)
            .supportsFileAttributeView(AclFileAttributeView.class)) {
            throw new IOException("Credential vault needs an owner-only filesystem.");
        }
        // Derive the actual file owner (which may be a group on Windows) from an empty probe, not the configured login
        // name.
        Path probe = Files.createTempFile(directory, ".vault-owner-", ".tmp");
        UserPrincipal owner;
        try {
            owner = Files.getOwner(probe, LinkOption.NOFOLLOW_LINKS);
        } finally {
            Files.deleteIfExists(probe);
        }
        final List<AclEntry> acl = Collections.singletonList(
            AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(owner)
                .setPermissions(EnumSet.allOf(AclEntryPermission.class))
                .build());
        Path temp = Files.createTempFile(directory, ".vault-", ".tmp", new FileAttribute<List<AclEntry>>() {

            @Override
            public String name() {
                return "acl:acl";
            }

            @Override
            public List<AclEntry> value() {
                return acl;
            }
        });
        boolean verified = false;
        try {
            // Some providers inherit permissions at creation. Reset and verify before writing any bytes.
            Files.getFileAttributeView(temp, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS)
                .setAcl(acl);
            verifyProtected(temp);
            verified = true;
            return temp;
        } finally {
            if (!verified) Files.deleteIfExists(temp);
        }
    }

    private static void verifyProtected(Path path) throws IOException {
        if (!Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
            .isRegularFile()) {
            throw new IOException("Invalid credential vault file.");
        }
        if (Files.getFileAttributeView(path, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS) != null) {
            Set<PosixFilePermission> actual = Files.getPosixFilePermissions(path, LinkOption.NOFOLLOW_LINKS);
            if (actual.contains(PosixFilePermission.OWNER_READ) && PRIVATE_MODE.containsAll(actual)) return;
        } else {
            AclFileAttributeView view = Files
                .getFileAttributeView(path, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            if (view != null) {
                UserPrincipal owner = view.getOwner();
                boolean ownerCanRead = false;
                for (AclEntry entry : view.getAcl()) {
                    if (entry.type() != AclEntryType.ALLOW) continue;
                    if (!owner.equals(entry.principal()))
                        throw new IOException("Credential vault ACL is not owner-only.");
                    ownerCanRead |= entry.permissions()
                        .contains(AclEntryPermission.READ_DATA);
                }
                if (ownerCanRead) return;
            }
        }
        throw new IOException("Credential vault file is not owner-only.");
    }
}
