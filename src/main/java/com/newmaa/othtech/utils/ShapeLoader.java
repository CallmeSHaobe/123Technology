package com.newmaa.othtech.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/** Loads external GZIP-compressed UTF-8 JSON structures without trimming or transposing their rows. */
public final class ShapeLoader {

    private static final int MAX_UNCOMPRESSED_BYTES = 64 * 1024 * 1024;
    private static final Gson GSON = new Gson();
    private static final Map<String, String[][]> CACHE = new ConcurrentHashMap<>();

    private ShapeLoader() {}

    /**
     * Decompress once per resource and return an independent array on each call. Strings, including
     * trailing spaces and empty rows, are retained exactly. Ragged layer sizes are valid StructureLib data.
     * Missing or corrupt resources fail explicitly instead of creating an accidentally empty structure.
     */
    public static String[][] loadCompressed(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith("/assets/123technology/structures/")
            || !resourcePath.endsWith(".shape.gz")
            || resourcePath.contains("..")) {
            throw new IllegalArgumentException("Invalid compressed structure resource: " + resourcePath);
        }
        String[][] cached = CACHE.computeIfAbsent(resourcePath, ShapeLoader::readCompressed);
        String[][] copy = new String[cached.length][];
        for (int layer = 0; layer < cached.length; layer++) {
            copy[layer] = cached[layer].clone();
        }
        return copy;
    }

    private static String[][] readCompressed(String resourcePath) {
        try (InputStream resource = ShapeLoader.class.getResourceAsStream(resourcePath)) {
            if (resource == null) throw new IOException("Resource not found");
            try (GZIPInputStream gzip = new GZIPInputStream(resource);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = gzip.read(buffer)) != -1) {
                    if (output.size() > MAX_UNCOMPRESSED_BYTES - count) {
                        throw new IOException("Expanded structure exceeds " + MAX_UNCOMPRESSED_BYTES + " bytes");
                    }
                    output.write(buffer, 0, count);
                }
                String[][] shape = GSON
                    .fromJson(new String(output.toByteArray(), StandardCharsets.UTF_8), String[][].class);
                if (shape == null || shape.length == 0) throw new IOException("Structure has no layers");
                for (int layer = 0; layer < shape.length; layer++) {
                    if (shape[layer] == null || shape[layer].length == 0) {
                        throw new IOException("Structure has a missing or empty layer " + layer);
                    }
                    for (String row : shape[layer]) {
                        if (row == null) throw new IOException("Structure has a null row in layer " + layer);
                    }
                }
                return shape;
            }
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("Cannot load compressed structure " + resourcePath, e);
        }
    }
}
