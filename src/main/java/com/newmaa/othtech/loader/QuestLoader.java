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
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gregtech.api.enums.Mods;

public class QuestLoader {

    private static final String MOD_RESOURCE_ID = "123technology";
    // Base64URL encoding of questLineIDHigh/Low from QuestLine.json (matches UuidConverter.encodeUuid format).
    // QuestLinesOrder.txt lines must start with the 24-char URL-safe Base64 UUID, not the directory name.
    private static final String QUEST_LINE_ORDER_KEY = "MmDiQmi9SX-d2PbI-qhBiw==: 123Technology";

    private static final File CONFIG_ORDER_FILE = new File(
        "config/" + Mods.BetterQuesting.ID + "/DefaultQuests/QuestLinesOrder.txt");
    private static final File CONFIG_QUESTS_DIR = new File("config/" + Mods.BetterQuesting.ID + "/DefaultQuests");
    private static final String RESOURCE_QUESTS_PREFIX = "assets/" + MOD_RESOURCE_ID + "/quest/DefaultQuests/";

    public static void registry() {
        try {
            syncQuestLinesOrder();
            copyDefaultQuestsFromJar();
        } catch (Exception e) {
            System.err.println("[QuestLoader-123T] Quest injection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void syncQuestLinesOrder() throws IOException {
        List<String> lines = readFileLines();

        // Remove any stale entry from a previous broken version of this mod.
        boolean removed = lines.removeIf(l -> l.startsWith("123TechQuestLine=="));

        if (!removed && lines.contains(QUEST_LINE_ORDER_KEY)) {
            System.out.println("[QuestLoader-123T] QuestLinesOrder.txt is already up-to-date.");
            return;
        }

        if (!lines.contains(QUEST_LINE_ORDER_KEY)) {
            lines.add(QUEST_LINE_ORDER_KEY);
        }
        writeFileLines(lines);
        System.out.println("[QuestLoader-123T] Updated QuestLinesOrder.txt with key: " + QUEST_LINE_ORDER_KEY);
    }

    public static void copyDefaultQuestsFromJar() throws IOException {
        // Use getProtectionDomain().getCodeSource().getLocation() so that URI decoding
        // handles spaces and special characters in the mod path correctly.
        URL codeSourceUrl;
        try {
            codeSourceUrl = QuestLoader.class.getProtectionDomain()
                .getCodeSource()
                .getLocation();
        } catch (SecurityException e) {
            System.out.println("[QuestLoader-123T] Unable to get code source location: " + e.getMessage());
            return;
        }

        if (codeSourceUrl == null) {
            System.out.println("[QuestLoader-123T] Code source URL is null (dev environment?)");
            return;
        }

        File jarFile;
        try {
            jarFile = new File(codeSourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new IOException("Failed to resolve jar path from URL: " + codeSourceUrl, e);
        }

        if (!jarFile.exists() || !jarFile.getName()
            .endsWith(".jar")) {
            System.out.println("[QuestLoader-123T] Not running from a jar file: " + jarFile);
            return;
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
