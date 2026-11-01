package com.newmaa.othtech.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 从 mod 资源里读取 StructureLib 形状，替代「几万条字符串字面量直接写在 Java 里」的写法。
 *
 * <p>
 * 起因：{@code OTEMegaEEC} 的 {@code shapeMain} 有 8008 条字符串字面量，作为<b>实例</b> {@code final}
 * 字段时 javac 会把这份初始化字节码复制进<b>每一个构造器</b>，实测两个构造器各 51 KB，
 * 而 JVM 单方法上限是 65,535 字节 —— 再往形状里加两千条就 {@code code too large}。
 * 外置成文本后构造器只剩 1 KB 级，且改结构不用重编译。
 * </p>
 *
 * <p>
 * 文件格式：UTF-8 纯文本，每个非空、非 {@code #} 开头的行是形状的一行（固定宽 {@code width}）；
 * 每 {@code height} 行构成一层，共 {@code layers} 层。空位用 {@link #EMPTY}（{@code '.'}）书写，
 * 加载时还原成 StructureLib 认识的空格。
 * </p>
 */
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
