package com.newmaa.othtech.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ShapeLoader {

    private static final Logger LOG = LogManager.getLogger("123Technology/ShapeLoader");

    /** 形状文件里的空位字符。用 '.' 而不是空格，避免被 IDE/脚本的 trim trailing whitespace 静默吃掉 */
    public static final char EMPTY = '.';

    private ShapeLoader() {}

    /**
     * 读取并校验形状。
     *
     * @param resourcePath classpath 绝对路径，例如 {@code "/assets/123technology/structures/megaeec_shape.txt"}
     * @param width        每行字符数（x 方向）
     * @param height       每层行数（z 方向）
     * @param layers       层数（y 方向）
     * @return {@code [layer][row]} 的形状；资源缺失或解析失败时返回空数组并记录 ERROR，不抛异常
     */
    public static String[][] load(String resourcePath, int width, int height, int layers) {
        if (width <= 0 || height <= 0 || layers <= 0) {
            LOG.error("ShapeLoader: invalid shape size {}x{}x{} for {}", width, height, layers, resourcePath);
            return new String[0][];
        }
        List<String> rows = new ArrayList<>(height * layers);
        try (InputStream in = ShapeLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                LOG.error("ShapeLoader: resource {} not found, structure will be empty", resourcePath);
                return new String[0][];
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.charAt(0) == '#') continue;
                if (line.length() > width) {
                    LOG.error(
                        "ShapeLoader: {}:{} is {} chars, wider than {}; truncated",
                        resourcePath,
                        lineNo,
                        line.length(),
                        width);
                    line = line.substring(0, width);
                } else if (line.length() < width) {
                    LOG.error(
                        "ShapeLoader: {}:{} is {} chars, narrower than {}; padded",
                        resourcePath,
                        lineNo,
                        line.length(),
                        width);
                    line = String.format("%-" + width + "s", line);
                }
                rows.add(line.replace(EMPTY, ' '));
            }
        } catch (Exception e) {
            LOG.error("ShapeLoader: failed to read " + resourcePath, e);
            return new String[0][];
        }

        int expected = height * layers;
        if (rows.size() != expected) {
            LOG.error(
                "ShapeLoader: {} has {} shape rows, expected {} ({}x{})",
                resourcePath,
                rows.size(),
                expected,
                height,
                layers);
        }
        String[][] shape = new String[layers][height];
        for (int y = 0; y < layers; y++) {
            for (int z = 0; z < height; z++) {
                int idx = y * height + z;
                shape[y][z] = idx < rows.size() ? rows.get(idx) : "";
            }
        }
        LOG.info("ShapeLoader: loaded {} ({} layers x {} rows x {} cols)", resourcePath, layers, height, width);
        return shape;
    }
}
