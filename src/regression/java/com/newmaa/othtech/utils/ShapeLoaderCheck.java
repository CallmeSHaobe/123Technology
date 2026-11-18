package com.newmaa.othtech.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/** Standalone regression: real resource loading, exact whitespace, cache isolation, and corruption handling. */
public final class ShapeLoaderCheck {

    public static void main(String[] args) throws Exception {
        Path resourceRoot = Paths.get(args[0]);
        Path fixtures = resourceRoot.resolve("assets/123technology/structures/check");
        Files.createDirectories(fixtures);
        write(fixtures.resolve("exact.shape.gz"), "[[\" ~ \",\"\",\"A  \"],[\"B\"]]");
        String path = "/assets/123technology/structures/check/exact.shape.gz";
        String[][] shape = ShapeLoader.loadCompressed(path);
        if (!Arrays.deepEquals(shape, new String[][] { { " ~ ", "", "A  " }, { "B" } })) {
            throw new AssertionError("Whitespace or ragged layers changed");
        }
        shape[0][0] = "mutated";
        shape[1] = new String[] { "replaced" };
        if (!ShapeLoader.loadCompressed(path)[0][0].equals(" ~ ")
            || !ShapeLoader.loadCompressed(path)[1][0].equals("B")) {
            throw new AssertionError("Caller modified cached data");
        }
        write(fixtures.resolve("invalid.shape.gz"), "[[null]]");
        expectFailure("/assets/123technology/structures/check/invalid.shape.gz");
        Files.write(fixtures.resolve("corrupt.shape.gz"), "not gzip".getBytes(StandardCharsets.UTF_8));
        expectFailure("/assets/123technology/structures/check/corrupt.shape.gz");
        expectFailure("/assets/123technology/structures/check/missing.shape.gz");
        int loaded = 0;
        Path realRoot = Paths.get(args[1]);
        try (Stream<Path> paths = Files.walk(realRoot)) {
            for (Path file : (Iterable<Path>) paths.filter(
                p -> p.toString()
                    .endsWith(".shape.gz"))::iterator) {
                String relative = realRoot.relativize(file)
                    .toString()
                    .replace('\\', '/');
                String[][] actual = ShapeLoader.loadCompressed("/assets/123technology/structures/ote/" + relative);
                if (actual.length == 0) throw new AssertionError("Empty resource: " + file);
                loaded++;
            }
        }
        System.out.println(
            "PASS: " + loaded
                + " production structures; exact whitespace, ragged layers, cache isolation, corrupt/missing resource rejection");
    }

    private static void write(Path path, String json) throws Exception {
        try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(path))) {
            gzip.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void expectFailure(String path) {
        try {
            ShapeLoader.loadCompressed(path);
            throw new AssertionError("Accepted invalid resource " + path);
        } catch (IllegalStateException expected) {
            // Missing/corrupt structures must never silently become valid empty machines.
        }
    }
}
