package com.newmaa.othtech.common.dialogue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/** Local-only Java 8 regression; never calls a provider or reads a real key. */
public final class AyanamiKeyVaultCheck {

    private static final String ENDPOINT = "https://api.example.invalid/v1/chat/completions?deployment=a";
    private static final String OTHER = "https://api.example.invalid/v1/chat/completions?deployment=b";
    private static final String KEY = "test-only-credential-never-use-01936f3cb4024ff6";
    private static final String NEXT = "test-only-credential-never-use-2a68d8dcc00d45db";

    private AyanamiKeyVaultCheck() {}

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("ayanami-vault-check-");
        Path dir = temp.resolve("install");
        try {
            verify(dir);
        } finally {
            if (Files.exists(dir)) {
                try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
                    for (Path file : files) Files.delete(file);
                }
                Files.delete(dir);
            }
            Files.delete(temp);
        }
        System.out.println(
            "Ayanami key vault: endpoint binding, atomic replacement, private files and fail-closed checks verified");
    }

    private static void verify(Path dir) throws Exception {
        AyanamiKeyVault vault = new AyanamiKeyVault(dir);
        check(vault.read(ENDPOINT) == null && !Files.exists(dir), "Read is non-creating");
        fails(() -> vault.save(ENDPOINT, " "), "Reject blank key");
        fails(
            () -> vault.save("https://name:password@api.example.invalid/chat/completions", KEY),
            "Reject URI user info");
        check(!Files.exists(dir), "Invalid input does not create vault");
        vault.save(ENDPOINT, KEY);
        AyanamiKeyVault reopened = new AyanamiKeyVault(dir);
        check(KEY.equals(reopened.read(ENDPOINT)), "Credential survives reopen");
        check(KEY.equals(reopened.read(ENDPOINT.replace("https://api", "HTTPS://API"))), "Normalize scheme/host case");
        check(reopened.read(OTHER) == null, "Different query must not receive credential");
        check(reopened.read(ENDPOINT.replace(".invalid/", ".invalid:443/")) == null, "Explicit port stays distinct");
        check(reopened.read(ENDPOINT.replace("/v1/", "/v2/")) == null, "Path stays distinct");
        check(reopened.read(ENDPOINT.replace("api.example", "api2.example")) == null, "Host stays distinct");
        Path master = dir.resolve("master.bin");
        Path data = dir.resolve("credential.gcm");
        checkPrivate(master);
        checkPrivate(data);
        byte[] firstMaster = Files.readAllBytes(master);
        byte[] original = Files.readAllBytes(data);
        checkNoPlaintext(dir);
        vault.save(ENDPOINT, KEY);
        check(!Arrays.equals(original, Files.readAllBytes(data)), "Fresh nonce on resave");
        check(Arrays.equals(firstMaster, Files.readAllBytes(master)), "Master stays fixed on update");
        reopened.save(OTHER, NEXT);
        check(vault.read(ENDPOINT) == null && NEXT.equals(vault.read(OTHER)), "Only one credential remains");
        checkNoPlaintext(dir);
        byte[] latest = Files.readAllBytes(data);
        byte[] tampered = latest.clone();
        tampered[4] ^= 0x10;
        overwrite(data, tampered);
        fails(() -> vault.read(OTHER), "Reject swapped binding even on mismatch");
        fails(() -> vault.save(ENDPOINT, KEY), "Do not overwrite tampered vault");
        check(Arrays.equals(tampered, Files.readAllBytes(data)), "Prior record unchanged on failure");
        overwrite(data, latest);
        tampered = latest.clone();
        tampered[tampered.length - 1] ^= 1;
        overwrite(data, tampered);
        fails(() -> vault.read(OTHER), "Reject tampered ciphertext/tag");
        overwrite(data, new byte[5000]);
        fails(() -> vault.read(OTHER), "Reject oversized record");
        overwrite(data, latest);
        concurrency(vault, reopened);
        Files.delete(master);
        fails(() -> vault.read(ENDPOINT), "Missing master means fail closed");
        fails(() -> vault.save(ENDPOINT, KEY), "Never regenerate a missing master for existing ciphertext");
        check(!Files.exists(master), "No replacement master was created");
    }

    private static void concurrency(AyanamiKeyVault a, AyanamiKeyVault b) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        Thread left = writer(a, KEY, start, thrown);
        Thread right = writer(b, NEXT, start, thrown);
        left.start();
        right.start();
        start.countDown();
        left.join();
        right.join();
        if (thrown.get() != null) throw new AssertionError("Concurrent vault writes failed", thrown.get());
        String value = a.read(ENDPOINT);
        check(KEY.equals(value) || NEXT.equals(value), "Concurrent writes leave complete replacement");
    }

    private static Thread writer(AyanamiKeyVault vault, String key, CountDownLatch start,
        AtomicReference<Throwable> thrown) {
        return new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 4; i++) vault.save(ENDPOINT, key);
            } catch (Throwable e) {
                thrown.compareAndSet(null, e);
            }
        });
    }

    private static void checkPrivate(Path file) throws IOException {
        if (Files.getFileStore(file)
            .supportsFileAttributeView(PosixFileAttributeView.class)) {
            check(
                Files.getPosixFilePermissions(file)
                    .equals(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)),
                "POSIX secret files use mode 0600");
        } else {
            AclFileAttributeView acl = Files.getFileAttributeView(file, AclFileAttributeView.class);
            for (AclEntry entry : acl.getAcl()) {
                if (entry.type() == AclEntryType.ALLOW) check(
                    entry.principal()
                        .equals(acl.getOwner()),
                    "Owner-only ACL");
            }
        }
    }

    private static void checkNoPlaintext(Path dir) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
            for (Path file : files) {
                String bytes = new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1);
                check(
                    !bytes.contains(KEY) && !bytes.contains(NEXT)
                        && !bytes.contains(ENDPOINT)
                        && !bytes.contains(OTHER),
                    "No plaintext credential or endpoint on disk");
            }
        }
    }

    private static void overwrite(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void check(boolean value, String reason) {
        if (!value) throw new AssertionError(reason);
    }

    private static void fails(Operation work, String reason) throws Exception {
        try {
            work.run();
        } catch (IOException expected) {
            return;
        }
        throw new AssertionError(reason);
    }

    private interface Operation {

        void run() throws Exception;
    }
}
