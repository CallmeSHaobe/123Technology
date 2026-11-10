/*
 * Usage: java tools/DepCheck.java <modJar> <classpathFile> [<classpathFile> ...]
 * classpathFile = one jar path per line (runtimeClasspath dump or a crash report's launch classpath).
 */
import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class DepCheck {

    public static void main(String[] args) throws Exception {
        Path modJar = Path.of(args[0]);
        Set<String> available = new HashSet<>();
        int jars = 0;
        for (int a = 1; a < args.length; a++) {
            for (String line : Files.readAllLines(Path.of(args[a]))) {
                if (line.isBlank()) continue;
                File f = new File(line.trim());
                if (!f.isFile()) continue;
                jars++;
                try (ZipFile zip = new ZipFile(f)) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        String n = entries.nextElement()
                            .getName();
                        if (!n.endsWith(".class")) continue;
                        String c = n.substring(0, n.length() - 6);
                        available.add(c);
                        int d = c.indexOf('$');
                        if (d > 0) available.add(c.substring(0, d));
                    }
                } catch (Exception e) {
                    System.out.println("BROKEN: " + f + " (" + e.getMessage() + ")");
                }
            }
        }

        Map<String, Integer> refs = new TreeMap<>();
        Map<String, Set<String>> referrers = new TreeMap<>();
        int classes = 0;
        try (ZipFile zip = new ZipFile(modJar.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.getName()
                    .endsWith(".class")) continue;
                classes++;
                String self = entry.getName()
                    .replace('/', '.')
                    .replace(".class", "");
                for (String name : referencedClasses(zip.getInputStream(entry).readAllBytes())) {
                    int d = name.indexOf('$');
                    if (d > 0) name = name.substring(0, d);
                    refs.merge(name, 1, Integer::sum);
                    referrers.computeIfAbsent(name, k -> new TreeSet<>())
                        .add(self);
                }
            }
        }

        // Provided by the JDK/FML/Minecraft/launcher, or by the mod jar itself.
        String[] skipRoots = { "java/", "javax/", "sun/", "jdk/", "com/sun/", "org/w3c/", "org/xml/",
            "net/minecraft/", "net/minecraftforge/", "cpw/", "org/lwjgl/", "io/netty/", "gnu/trove/",
            "com/google/", "org/apache/", "scala/", "org/jetbrains/", "org/spongepowered/",
            modJarRoot(modJar) };

        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, Integer> e : refs.entrySet()) {
            String name = e.getKey();
            if (name.length() < 4 || !name.contains("/")) continue;
            boolean skip = false;
            for (String r : skipRoots) {
                if (name.startsWith(r)) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            if (!available.contains(name)) missing.add(name + "\trefs=" + e.getValue());
        }

        System.out.println("runtime jars        : " + jars);
        System.out.println("mod classes         : " + classes);
        System.out.println("distinct class refs : " + refs.size());
        System.out.println();
        System.out.println("MISSING on classpath: " + missing.size());
        for (String m : missing) {
            System.out.println("  " + m);
            String cls = m.substring(0, m.indexOf('\t'));
            for (String r : referrers.getOrDefault(cls, Set.of())) System.out.println("      <- " + r);
        }
        if (!missing.isEmpty()) System.exit(1);
    }

    private static String modJarRoot(Path jar) {
        // The mod's own package never needs to be resolved against the classpath.
        return jar.getFileName()
            .toString()
            .startsWith("TwistSpaceTechnology") ? "com/Nxer/" : "\u0000";
    }

    /** Class names from the CONSTANT_Class entries of a class file. */
    private static List<String> referencedClasses(byte[] bytes) throws Exception {
        DataInputStream in = new DataInputStream(new java.io.ByteArrayInputStream(bytes));
        in.readInt(); // magic
        in.readUnsignedShort(); // minor
        in.readUnsignedShort(); // major
        int count = in.readUnsignedShort() - 1;

        String[] utf8 = new String[count + 1];
        List<Integer> classRefs = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1 -> { // Utf8
                    int len = in.readUnsignedShort();
                    utf8[i] = new String(in.readNBytes(len), java.nio.charset.StandardCharsets.UTF_8);
                }
                case 7 -> classRefs.add(in.readUnsignedShort()); // Class
                case 8 -> in.skipBytes(2); // String
                case 3, 4, 9, 10, 11, 12, 17, 18 -> in.skipBytes(4); // Integer/Float/refs
                case 5, 6 -> { in.skipBytes(8); i++; } // Long/Double take two slots
                case 15 -> in.skipBytes(3); // MethodHandle
                case 16, 19, 20 -> in.skipBytes(2); // MethodType/Module/Package
                default -> throw new IllegalStateException("constant pool tag " + tag + " at index " + i);
            }
        }

        List<String> out = new ArrayList<>(classRefs.size());
        for (int idx : classRefs) {
            String name = utf8[idx];
            if (name == null) continue;
            while (name.startsWith("[")) name = name.substring(1);
            if (name.startsWith("L") && name.endsWith(";")) name = name.substring(1, name.length() - 1);
            out.add(name);
        }
        return out;
    }
}
