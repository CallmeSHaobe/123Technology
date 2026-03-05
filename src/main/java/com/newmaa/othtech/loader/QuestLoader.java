package com.newmaa.othtech.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gregtech.api.enums.Mods;

public class QuestLoader {

    private static final String MOD_RESOURCE_ID = "123technology";
    private static final String QUEST_LINE_ORDER_KEY = "123TechQuestLine==: 123Technology";

    private static final File CONFIG_ORDER_FILE = new File(
        "config/" + Mods.BetterQuesting.ID + "/DefaultQuests/QuestLinesOrder.txt");
    private static final File CONFIG_QUESTS_DIR = new File("config/" + Mods.BetterQuesting.ID + "/DefaultQuests");
    private static final String RESOURCE_QUESTS_PREFIX = "assets/" + MOD_RESOURCE_ID + "/quest/DefaultQuests/";

    public static void registry() {
        try {
            syncQuestLinesOrder();
            copyDefaultQuestsFromJar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void syncQuestLinesOrder() throws IOException {
        List<String> lines = readFileLines();

        if (lines.contains(QUEST_LINE_ORDER_KEY)) {
            System.out.println("[QuestLoader-123T] QuestLinesOrder.txt is already up-to-date.");
            return;
        }

        lines.add(QUEST_LINE_ORDER_KEY);
        writeFileLines(lines);
        System.out.println("[QuestLoader-123T] Appended " + QUEST_LINE_ORDER_KEY + " to QuestLinesOrder.txt.");
    }

    public static void copyDefaultQuestsFromJar() throws IOException {
        String path = Objects.requireNonNull(
            QuestLoader.class.getResource(
                "/" + QuestLoader.class.getName()
                    .replace('.', '/') + ".class"))
            .toString();

        if (!path.startsWith("jar:file:")) {
            System.out.println("[QuestLoader-123T] Not running from a jar file: " + path);
            return;
        }

        String jarPath = path.substring("jar:file:".length(), path.indexOf("!"));
        File jarFile = new File(jarPath);

        if (!jarFile.exists() || !jarFile.getName()
            .endsWith(".jar")) {
            System.out.println("[QuestLoader-123T] Resolved jar path not found: " + jarPath);
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
