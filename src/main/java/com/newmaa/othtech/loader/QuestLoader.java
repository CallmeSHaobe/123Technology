package com.newmaa.othtech.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gregtech.api.enums.Mods;

public class QuestLoader {

    private static final String MOD_RESOURCE_ID = "123technology";
    // URL-safe Base64 encoding of (questLineIDHigh, questLineIDLow) from QuestLine.json.
    // Must be a valid 24-char Base64URL UUID as required by UuidConverter.decodeUuid().
    private static final String QUEST_LINE_UUID = "MmDiQmi9SX-d2PbI-qhBiw==";
    private static final String QUEST_LINE_ORDER_KEY = QUEST_LINE_UUID + ": 123Technology";

    private static final File CONFIG_ORDER_FILE = new File(
        "config/" + Mods.BetterQuesting.ID + "/DefaultQuests/QuestLinesOrder.txt");
    private static final File CONFIG_QUESTS_DIR = new File("config/" + Mods.BetterQuesting.ID + "/DefaultQuests");
    private static final String RESOURCE_QUESTS_PREFIX = "assets/" + MOD_RESOURCE_ID + "/quest/DefaultQuests/";

    public static void registry() {
        try {
            // IMPORTANT: copy quest files first, then inject the order key.
            // Injecting the key without the corresponding QuestLine.json causes
            // BetterQuesting to store a null IQuestLine entry, which crashes the
            // quest-book GUI with NullPointerException at refreshChapterVisibility.
            boolean filesCopied = copyDefaultQuestsFromJar();
            syncQuestLinesOrder(filesCopied);
        } catch (Exception e) {
            System.err.println("[QuestLoader-123T] Quest injection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Injects or removes our quest line order key depending on whether quest files are present.
     *
     * @param injectKey true → ensure our UUID key is in the file (files are confirmed present)
     *                  false → remove our UUID key if present (files unavailable; prevents null
     *                  IQuestLine entry in BetterQuesting which causes NPE in the quest-book GUI)
     */
    public static void syncQuestLinesOrder(boolean injectKey) throws IOException {
        List<String> lines = readFileLines();

        // Always remove the old broken key written by earlier versions of this mod.
        boolean removedStale = lines.removeIf(l -> l.startsWith("123TechQuestLine=="));

        if (!injectKey) {
            // Files aren't available — also remove our new key to prevent a null IQuestLine entry.
            boolean removedNew = lines.removeIf(l -> l.startsWith(QUEST_LINE_UUID));
            if (removedStale || removedNew) {
                writeFileLines(lines);
                System.out.println("[QuestLoader-123T] Removed stale quest order keys (files unavailable).");
            }
            return;
        }

        if (!removedStale && lines.contains(QUEST_LINE_ORDER_KEY)) {
            System.out.println("[QuestLoader-123T] QuestLinesOrder.txt is already up-to-date.");
            return;
        }

        if (!lines.contains(QUEST_LINE_ORDER_KEY)) {
            lines.add(QUEST_LINE_ORDER_KEY);
        }
        writeFileLines(lines);
        System.out.println("[QuestLoader-123T] Updated QuestLinesOrder.txt with key: " + QUEST_LINE_ORDER_KEY);
    }

    /**
     * Copies quest resource files from the mod jar into the BetterQuesting DefaultQuests directory.
     *
     * @return true if the jar was found and files were managed (even if all were already up-to-date);
     *         false if not running from a jar (e.g. dev environment) — caller should not inject the
     *         order key in this case.
     */
    public static boolean copyDefaultQuestsFromJar() throws IOException {
        File jarFile = resolveJarFile();
        if (jarFile == null) {
            System.out.println("[QuestLoader-123T] Not running from a jar file — skipping quest copy.");
            return false;
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;

                String name = entry.getName();
                if (!name.startsWith(RESOURCE_QUESTS_PREFIX)) continue;

                String relativePath = name.substring(RESOURCE_QUESTS_PREFIX.length());
                File targetFile = new File(CONFIG_QUESTS_DIR, relativePath);

                boolean shouldCopy = true;
                if (targetFile.exists()) {
                    try (InputStream compareStream = jar.getInputStream(entry)) {
                        if (compareFileContent(compareStream, targetFile)) {
                            shouldCopy = false;
                        }
                    }
                }

                if (shouldCopy) {
                    targetFile.getParentFile()
                        .mkdirs();
                    try (InputStream freshStream = jar.getInputStream(entry);
                        OutputStream out = new FileOutputStream(targetFile)) {
                        copyStream(freshStream, out);
                        System.out.println("[QuestLoader-123T] Copied/Updated: " + targetFile.getName());
                    }
                }
            }
        }
        return true;
    }

    /**
     * Resolves the jar file containing this class using two independent methods.
     * <ol>
     * <li>ProtectionDomain / CodeSource — direct, works in most Forge production setups.</li>
     * <li>Class resource URL — reliable fallback that also handles percent-encoded paths
     * (e.g. spaces in the mods folder path).</li>
     * </ol>
     *
     * @return the jar {@link File}, or {@code null} if not running from a jar.
     */
    private static File resolveJarFile() {
        // Method 1: getProtectionDomain().getCodeSource().getLocation()
        try {
            ProtectionDomain pd = QuestLoader.class.getProtectionDomain();
            if (pd != null) {
                CodeSource cs = pd.getCodeSource();
                if (cs != null) {
                    URL loc = cs.getLocation();
                    if (loc != null) {
                        try {
                            File f = new File(loc.toURI());
                            if (f.isFile() && f.getName()
                                .endsWith(".jar")) {
                                return f;
                            }
                        } catch (URISyntaxException ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}

        // Method 2: class resource URL with proper URI percent-decode
        URL classUrl = QuestLoader.class.getResource(
            "/" + QuestLoader.class.getName()
                .replace('.', '/') + ".class");
        if (classUrl == null) return null;

        String urlStr = classUrl.toString();
        if (!urlStr.startsWith("jar:file:")) return null;

        int bangIdx = urlStr.indexOf("!/");
        if (bangIdx < 0) return null;

        try {
            // "jar:file:/path/to/mod.jar!/..." → parse "file:/path/to/mod.jar" as URI
            // so that percent-encoded characters (e.g. %20) are decoded correctly.
            File f = new File(new URI(urlStr.substring("jar:".length(), bangIdx)));
            return (f.isFile() && f.getName()
                .endsWith(".jar")) ? f : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean compareFileContent(InputStream in1, File file2) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash1;
            try (DigestInputStream dis1 = new DigestInputStream(in1, digest)) {
                while (dis1.read() != -1) {}
                hash1 = digest.digest();
            }

            digest.reset();

            byte[] hash2;
            try (InputStream fis = new FileInputStream(file2);
                DigestInputStream dis2 = new DigestInputStream(fis, digest)) {
                while (dis2.read() != -1) {}
                hash2 = digest.digest();
            }

            return Arrays.equals(hash1, hash2);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    public static List<String> readFileLines() throws IOException {
        List<String> lines = new ArrayList<>();
        if (!CONFIG_ORDER_FILE.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) lines.add(trimmed);
            }
        }
        return lines;
    }

    public static void writeFileLines(List<String> lines) throws IOException {
        CONFIG_ORDER_FILE.getParentFile()
            .mkdirs();
        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
