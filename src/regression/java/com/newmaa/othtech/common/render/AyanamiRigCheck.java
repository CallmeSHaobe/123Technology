package com.newmaa.othtech.common.render;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.newmaa.othtech.common.blocks.AyanamiAction;
import com.newmaa.othtech.common.blocks.AyanamiBlink;

/** Offline checks for the actual shipped rig and mesh; no Minecraft or OpenGL context. */
public final class AyanamiRigCheck {

    public static void main(String[] args) throws Exception {
        AyanamiRig rig;
        try (BufferedReader reader = resource("Ayanami.rig.json")) {
            rig = AyanamiRig.read(reader);
        }
        Map<String, List<double[][]>> meshes = readMeshes("Ayanami.obj");
        int faces = meshes.values()
            .stream()
            .mapToInt(List::size)
            .sum();
        check(rig.matchesMeshes(meshes.keySet()), "Every group must be bound exactly once");
        check(faces == 995, "Unexpected face count in standing OBJ");
        check(rig.root().children.size() == 5, "Missing body joints");
        checkGarmentFit(rig, meshes);
        checkEyes(rig, meshes);
        checkBlink(rig, meshes, readMeshes("AyanamiBlink.obj").get("blink_lids"));
        check(AyanamiAction.fromId("") == AyanamiAction.STANDING, "Legacy state defaults to standing");
        AyanamiAction action = AyanamiAction.STANDING;
        for (AyanamiAction expected : new AyanamiAction[] { AyanamiAction.STANDING, AyanamiAction.NOD,
            AyanamiAction.LOOK_AROUND, AyanamiAction.WAVE }) {
            check(action == expected && AyanamiAction.fromId(action.getId()) == action, "Invalid preset ID or order");
            action = action.next();
        }
        check(action == AyanamiAction.STANDING, "Action cycle must wrap");

        float[] rotation = new float[3];
        AyanamiRig.Clip nod = rig.clip(AyanamiAction.NOD);
        nod.sample("head", 4.5f, rotation);
        near(-8.5f, rotation[0]);
        nod.sample("head", 9, rotation);
        near(-17, rotation[0]);
        nod.sample("left_arm", 18, rotation);
        near(0, rotation[0]);
        near(0, rotation[1]);
        near(0, rotation[2]);
        near(0.5f, nod.loopTime(76, 0.5f));
        near(0, nod.loopTime(-1, 0.5f));
        rig.clip(AyanamiAction.LOOK_AROUND)
            .sample("head", 19, rotation);
        near(24, rotation[1]);
        rig.clip(AyanamiAction.WAVE)
            .sample("right_arm", 16, rotation);
        near(106, rotation[2]);
        AyanamiRig.Clip standing = rig.clip(AyanamiAction.STANDING);
        standing.sample("right_arm", 0, rotation);
        near(0, rotation[2]);
        near(0, AyanamiRig.walkingStride(1, 0));
        near(1, AyanamiRig.walkingStride(0, 2));
        near(0.5f, AyanamiRig.walkingStride(0, 0.2f));
        near(-1, AyanamiRig.walkingStride(4 * 0.35f, 1));
        near(1, AyanamiRig.walkingStride(4 * 0.70f, 1));
        standing.samplePose("right_leg", 0, 1, -30, 10, rotation);
        near(17, rotation[0]);
        standing.samplePose("left_leg", 0, 1, -30, 10, rotation);
        near(-17, rotation[0]);
        standing.samplePose("right_arm", 0, 0, -30, 10, rotation);
        near(0, rotation[0]);
        standing.samplePose("left_arm", 0, 0, -30, 10, rotation);
        near(0, rotation[0]);
        standing.samplePose("right_arm", 0, 1, -30, 10, rotation);
        near(-5, rotation[0]);
        standing.samplePose("left_arm", 0, 1, -30, 10, rotation);
        near(5, rotation[0]);
        standing.samplePose("head", 0, 1, -30, 10, rotation);
        near(10, rotation[0]);
        near(-30, rotation[1]);
        nod.samplePose("head", 9, 1, -30, 10, rotation);
        near(-17, rotation[0]);
        near(0, rotation[1]);
        AyanamiRig.Clip wave = rig.clip(AyanamiAction.WAVE);
        wave.samplePose("right_arm", 16, 1, -30, 10, rotation);
        near(-12, rotation[0]);
        near(106, rotation[2]);
        wave.samplePose("left_leg", 16, 1, -30, 10, rotation);
        near(-17, rotation[0]);
        checkHeldItem(rig, meshes);
        AyanamiRig.Clip strike = rig.clip(AyanamiAction.STRIKE);
        strike.sample("right_arm", 4, rotation);
        near(72, rotation[0]);
        strike.sample(AyanamiRig.HELD_ITEM_SOCKET, 4, rotation);
        near(15, rotation[0]);
        near(10, strike.oneShotTime(30, 0.5F));
        AyanamiRig.Clip thrust = rig.clip(AyanamiAction.THRUST);
        thrust.sample("right_arm", 3, rotation);
        near(79, rotation[0]);
        thrust.sample(AyanamiRig.HELD_ITEM_SOCKET, 3, rotation);
        near(-105, rotation[0]);
        check(
            Math.abs((79 + 15 + rotation[0] + AyanamiRig.LONGINUS_GRIP_PITCH) + 90) < 10,
            "Longinus must point forward, not upward, on the thrust hit frame");
        near(14, thrust.oneShotTime(30, 0.5F));
        System.out
            .println("Ayanami rig: " + faces + " OBJ faces; clips, gait, gaze, blink, garments and grip verified");
    }

    private static void checkHeldItem(AyanamiRig rig, Map<String, List<double[][]>> meshes) {
        AyanamiRig.Bone rightArm = rig.root().children.stream()
            .filter(bone -> "right_arm".equals(bone.name))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing right arm"));
        AyanamiRig.Bone leftArm = rig.root().children.stream()
            .filter(bone -> "left_arm".equals(bone.name))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing left arm"));
        check(
            rightArm.children.size() == 1 && AyanamiRig.HELD_ITEM_SOCKET.equals(rightArm.children.get(0).name),
            "Held item must inherit the animated right arm");
        AyanamiRig.Bone hand = rightArm.children.get(0);
        double[] rightPalm = { hand.pivot[0], hand.pivot[1], hand.pivot[2] };
        double[] leftPalm = { -hand.pivot[0], hand.pivot[1], hand.pivot[2] };
        for (int axis = 0; axis < rightPalm.length; axis++) {
            final int component = axis;
            double min = meshes.get("right_arm")
                .stream()
                .flatMap(java.util.Arrays::stream)
                .mapToDouble(vertex -> vertex[component])
                .min()
                .orElseThrow(AssertionError::new);
            double max = meshes.get("right_arm")
                .stream()
                .flatMap(java.util.Arrays::stream)
                .mapToDouble(vertex -> vertex[component])
                .max()
                .orElseThrow(AssertionError::new);
            check(min < rightPalm[axis] && rightPalm[axis] < max, "Standing socket must stay inside the mesh bounds");
        }
        AyanamiRig.Clip awaken = rig.clip(AyanamiAction.AWAKENING);
        near(0, awaken.oneShotTime(-1, 0.5F));
        near(42.5F, awaken.oneShotTime(42, 0.5F));
        near(84, awaken.oneShotTime(400, 0.5F));
        float[] armAngle = new float[3], wristAngle = new float[3], leftAngle = new float[3];
        awaken.sample(hand.name, 84, wristAngle);
        near(0, wristAngle[1]);
        awaken.sample(rightArm.name, 40, armAngle);
        awaken.sample(hand.name, 40, wristAngle);
        awaken.sample(leftArm.name, 40, leftAngle);
        double[] right = rotatePoint(rightPalm, rightArm.pivot, armAngle);
        double[] left = rotatePoint(leftPalm, leftArm.pivot, leftAngle);
        double[] shaft = rotatePoint(
            new double[] { 0, 1, 0 },
            new float[3],
            new float[] { AyanamiRig.LONGINUS_GRIP_PITCH, 0, 0 });
        shaft = rotatePoint(rotatePoint(shaft, new float[3], wristAngle), new float[3], armAngle);
        double[] brace = { left[0] - right[0], left[1] - right[1], left[2] - right[2] };
        double along = dot(brace, shaft);
        check(
            along > 0.15 && along < 0.40 && dot(brace, brace) - along * along < 0.03 * 0.03,
            "Left hand should brace the same shaft that the right wrist turns");
    }

    private static double[] rotatePoint(double[] point, float[] pivot, float[] degrees) {
        double x = point[0] - pivot[0], y = point[1] - pivot[1], z = point[2] - pivot[2];
        double a = Math.toRadians(degrees[0]), b = Math.toRadians(degrees[1]), c = Math.toRadians(degrees[2]);
        double nextY = y * Math.cos(a) - z * Math.sin(a);
        z = y * Math.sin(a) + z * Math.cos(a);
        double nextX = x * Math.cos(b) + z * Math.sin(b);
        z = -x * Math.sin(b) + z * Math.cos(b);
        return new double[] { nextX * Math.cos(c) - nextY * Math.sin(c) + pivot[0],
            nextX * Math.sin(c) + nextY * Math.cos(c) + pivot[1], z + pivot[2] };
    }

    private static Map<String, List<double[][]>> readMeshes(String filename) throws Exception {
        Map<String, List<double[][]>> meshes = new LinkedHashMap<>();
        List<double[]> vertices = new ArrayList<>();
        try (BufferedReader reader = resource(filename)) {
            List<double[][]> group = null;
            for (String line; (line = reader.readLine()) != null;) {
                if (line.startsWith("g ") || line.startsWith("o ")) {
                    group = new ArrayList<>();
                    check(meshes.put(line.substring(2), group) == null, "Duplicate OBJ group");
                } else if (line.startsWith("v ")) {
                    String[] fields = line.split("\\s+");
                    vertices.add(
                        new double[] { Double.parseDouble(fields[1]), Double.parseDouble(fields[2]),
                            Double.parseDouble(fields[3]) });
                } else if (line.startsWith("f ")) {
                    String[] fields = line.split("\\s+");
                    check(group != null && fields.length == 5, "Ungrouped or non-quad face");
                    double[][] face = new double[4][];
                    for (int i = 0; i < 4; i++) {
                        face[i] = vertices.get(Integer.parseInt(fields[i + 1].split("/")[0]) - 1);
                    }
                    group.add(face);
                }
            }
        }
        return meshes;
    }

    private static void checkBlink(AyanamiRig rig, Map<String, List<double[][]>> meshes, List<double[][]> lids) {
        AyanamiBlink blink = new AyanamiBlink();
        Random random = new Random(342);
        int last = 0, shortest = 71, longest = 0;
        boolean wasClosing = false;
        for (int tick = 1; tick <= 12000; tick++) {
            blink.tick(random);
            float amount = blink.closure(0.5F);
            boolean closing = amount > 0;
            if (closing && !wasClosing) {
                shortest = Math.min(shortest, tick - last);
                longest = Math.max(longest, tick - last);
                near(0, blink.closure(0));
                check(amount < 1, "Blink should start smoothly");
                last = tick;
            }
            if (closing && tick - last == 2) near(1, amount);
            if (wasClosing && !closing) check(tick - last == 5, "Blink must last five ticks");
            wasClosing = closing;
        }
        check(shortest == 40 && longest == 70, "Blink interval must cover 40–70 ticks");
        check(lids != null && lids.size() == 2, "Expected two blink panels");
        AyanamiRig.Bone head = rig.root().children.stream()
            .filter(bone -> "head".equals(bone.name))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing head"));
        for (AyanamiRig.Bone eye : head.children) {
            double[][] lid = lids.get(eye.pivot[0] > 0 ? 0 : 1);
            double[] n = normal(lid);
            check(n[2] < 0, "Blink panel must face out");
            for (double[] point : meshes.get(eye.name)
                .get(0)) {
                check(
                    point[0] - eye.pupilTravel[0] > lid[1][0] && point[0] + eye.pupilTravel[0] < lid[0][0]
                        && point[1] - eye.pupilTravel[1] > lid[0][1]
                        && point[1] + eye.maxPupilUpTravel() < lid[2][1],
                    "Closed lids must cover the full pupil travel");
                double[] relative = { point[0] - lid[0][0], point[1] - lid[0][1], point[2] - lid[0][2] };
                check(dot(n, relative) < 0, "Blink panel must sit in front of pupil");
            }
        }
    }

    private static void checkEyes(AyanamiRig rig, Map<String, List<double[][]>> meshes) {
        AyanamiRig.Bone head = rig.root().children.stream()
            .filter(bone -> "head".equals(bone.name))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing head"));
        check(head.children.size() == 2, "Both pupils must follow the head independently");
        float[] offset = new float[3];
        for (AyanamiRig.Bone eye : head.children) {
            List<double[][]> mesh = meshes.get(eye.name);
            check(mesh != null && mesh.size() == 1 && normal(mesh.get(0))[2] < 0, "Expected a front-facing eye plane");
            double[][] face = mesh.get(0);
            for (int axis = 0; axis < 3; axis++) {
                near(eye.pivot[axis], (float) ((face[0][axis] + face[1][axis] + face[2][axis] + face[3][axis]) / 4));
            }
            float centerY = eye.pivot[1];
            float centerZ = eye.pivot[2];
            float slope = eye.pupilTravel[2];
            AyanamiRig.Gaze ahead = AyanamiRig.Gaze.fromWorldOffset(0, centerY + slope * 2, 2 - centerZ, 180);
            check(ahead.pupilOffset(eye, offset) && offset[0] * eye.pivot[0] < 0, "Pupils must converge independently");
            near(0, offset[1]);
            float straightX = offset[0];
            AyanamiRig.Gaze quarterTurn = new AyanamiRig.Gaze(centerZ - 2, centerY + slope * 2, 0)
                .inBoneSpace(head.pivot, new float[] { 0, 90, 0 });
            check(quarterTurn.pupilOffset(eye, offset), "Head rotation was not removed before pupil tracking");
            near(straightX, offset[0]);
            new AyanamiRig.Gaze(8, centerY + 2, centerZ - 1).pupilOffset(eye, offset);
            check(
                offset[0] > 0 && offset[1] > 0
                    && offset[0] <= eye.pupilTravel[0]
                    && offset[1] <= eye.maxPupilUpTravel(),
                "Pupils must remain inside the eye whites");
            near(offset[1] * slope, offset[2]);
            new AyanamiRig.Gaze(eye.pivot[0], centerY + 1, centerZ - 1).pupilOffset(eye, offset);
            near(eye.maxPupilUpTravel(), offset[1]);
            check(offset[1] > eye.pupilTravel[1], "The extra eye white must allow more travel upward than downward");
            new AyanamiRig.Gaze(eye.pivot[0], centerY - 1, centerZ - 1).pupilOffset(eye, offset);
            near(-eye.pupilTravel[1], offset[1]);
            new AyanamiRig.Gaze(eye.pivot[0] + 2, centerY + slope, centerZ - 1).pupilOffset(eye, offset);
            near(eye.pupilTravel[0], offset[0]);
            near(0, offset[1]);
            check(
                !new AyanamiRig.Gaze(0, centerY, centerZ + 2).pupilOffset(eye, offset),
                "Targets behind the face must not pull the pupils out of their sockets");
        }
    }

    private static void checkGarmentFit(AyanamiRig rig, Map<String, List<double[][]>> meshes) {
        for (String side : new String[] { "right", "left" }) {
            String arm = side + "_arm";
            AyanamiRig.Bone shoulder = rig.root().children.stream()
                .filter(bone -> arm.equals(bone.name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing shoulder " + arm));
            // A pivot shared by the torso, limb and sleeve keeps them joined through the sweep.
            List<double[][]> armFaces = meshes.get(arm);
            check(armFaces.size() == 12, "Expected a fitted sleeve on " + arm);
            checkInside(shoulder.pivot, meshes.get("torso"), arm + " / torso");
            checkInside(shoulder.pivot, armFaces.subList(0, 6), arm + " / limb");
            checkInside(shoulder.pivot, armFaces.subList(6, 12), arm + " / sleeve");
            double[] armAxis = normal(armFaces.get(4));
            double[] sleeveAxis = normal(armFaces.get(10));
            double directionMatch = dot(armAxis, sleeveAxis)
                / Math.sqrt(dot(armAxis, armAxis) * dot(sleeveAxis, sleeveAxis));
            check(directionMatch > 0.999, "Sleeve no longer follows the upper arm axis: " + arm);
            check(armAxis[1] / Math.sqrt(dot(armAxis, armAxis)) > 0.96, "Resting arm is not hanging down: " + arm);
        }

        List<double[][]> skirt = meshes.get("skirt");
        double[][] liner = skirt.get(skirt.size() - 1);
        double height = liner[0][1];
        check(height > 0.48 && height < 0.55 && normal(liner)[1] < 0, "Missing downward-facing skirt liner");
        for (double[] point : liner) check(point[1] == height, "Skirt liner must be flat");
        double halfWidth = Math.abs(liner[0][0]);
        double halfDepth = Math.abs(liner[0][2]);
        check(halfWidth > 0.2 && halfDepth > 0.11, "Liner does not span both hips");
        for (String leg : new String[] { "right_leg", "left_leg" }) {
            for (double[][] face : meshes.get(leg)) {
                for (int i = 0; i < face.length; i++) {
                    double[] a = face[i], b = face[(i + 1) % face.length];
                    if ((a[1] < height) == (b[1] < height)) continue;
                    double t = (height - a[1]) / (b[1] - a[1]);
                    check(
                        Math.abs(a[0] + t * (b[0] - a[0])) + 0.01 < halfWidth
                            && Math.abs(a[2] + t * (b[2] - a[2])) + 0.01 < halfDepth,
                        "Liner exposes " + leg);
                }
            }
        }
        double hem = Double.POSITIVE_INFINITY;
        for (double[][] face : skirt) {
            for (double[] point : face) hem = Math.min(hem, point[1]);
        }
        check(hem > 0.40 && hem < 0.44 && hem < height, "Expected a shorter hem below the liner");
        double hemWidth = 0, midWidth = 0;
        for (double[][] face : skirt) {
            for (double[] point : face) {
                if (point[1] == hem) hemWidth = Math.max(hemWidth, Math.abs(point[0]));
                if (point[1] == height) midWidth = Math.max(midWidth, Math.abs(point[0]));
            }
        }
        check(Math.abs(hemWidth - midWidth) < 0.001, "Lower skirt should have straight sides, not a flared hem");

        double innerHemWidth = Double.POSITIVE_INFINITY, innerHemDepth = Double.POSITIVE_INFINITY;
        for (double[][] face : skirt) {
            for (double[] point : face) {
                if (point[1] != hem) continue;
                innerHemWidth = Math.min(innerHemWidth, Math.abs(point[0]));
                innerHemDepth = Math.min(innerHemDepth, Math.abs(point[2]));
            }
        }
        double rightInner = Double.POSITIVE_INFINITY, leftInner = Double.NEGATIVE_INFINITY;
        float[] legRotation = new float[3];
        AyanamiRig.Clip standing = rig.clip(AyanamiAction.STANDING);
        for (String side : new String[] { "right", "left" }) {
            AyanamiRig.Bone leg = rig.root().children.stream()
                .filter(bone -> (side + "_leg").equals(bone.name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing leg " + side));
            String shoe = side + "_shoe";
            check(
                java.util.Arrays.asList(leg.meshes)
                    .contains(shoe),
                "Shoe must move with its leg: " + side);
            List<double[][]> shoeFaces = meshes.get(shoe);
            check(shoeFaces.size() == 9, "Missing white foot wrap panels: " + side);
            check(
                shoeFaces.stream()
                    .anyMatch(face -> Math.abs(face[0][1]) < 0.001 && normal(face)[1] < 0),
                "Foot wrap must cover the underside: " + side);
            List<double[][]> legFaces = meshes.get(leg.name);
            double[][] foot = legFaces.get(5);
            for (int phase = 0; phase < 64; phase++) {
                float stride = (float) Math.cos(phase * (2 * Math.PI / 64));
                standing.samplePose(leg.name, 0, stride, 0, 0, legRotation);
                double angle = Math.toRadians(legRotation[0]);
                checkSwingClearance(legFaces, leg.pivot, angle, hem, innerHemWidth, innerHemDepth);
                checkSwingClearance(legFaces, leg.pivot, angle, height, halfWidth, halfDepth);
            }
            for (double[][] face : shoeFaces) {
                for (double[] point : face) {
                    if ("right".equals(side)) rightInner = Math.min(rightInner, point[0]);
                    else leftInner = Math.max(leftInner, point[0]);
                    check(point[1] < 0.05 && point[1] > -0.001, "White foot wrap must be one pixel high: " + side);
                    for (int axis : new int[] { 0, 2 }) {
                        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
                        for (double[] corner : foot) {
                            min = Math.min(min, corner[axis]);
                            max = Math.max(max, corner[axis]);
                        }
                        check(
                            point[axis] >= min - 0.006 && point[axis] <= max + 0.006,
                            "White foot wrap must follow the foot without a projecting toe: " + side);
                    }
                }
            }
        }
        check(
            rightInner - leftInner > 0.02 && rightInner - leftInner < 0.04,
            "Resting feet should be close but not touch");
    }

    private static void checkSwingClearance(List<double[][]> faces, float[] pivot, double angle, double height,
        double halfWidth, double halfDepth) {
        double sin = Math.sin(angle), cos = Math.cos(angle);
        for (double[][] face : faces) {
            for (int i = 0; i < face.length; i++) {
                double[] a = face[i], b = face[(i + 1) % face.length];
                double ay = pivot[1] + (a[1] - pivot[1]) * cos - (a[2] - pivot[2]) * sin;
                double by = pivot[1] + (b[1] - pivot[1]) * cos - (b[2] - pivot[2]) * sin;
                if ((ay < height) == (by < height)) continue;
                double t = (height - ay) / (by - ay);
                double x = a[0] + t * (b[0] - a[0]);
                double z = pivot[2] + (a[1] + t * (b[1] - a[1]) - pivot[1]) * sin
                    + (a[2] + t * (b[2] - a[2]) - pivot[2]) * cos;
                check(
                    Math.abs(x) + 0.003 < halfWidth && Math.abs(z) + 0.003 < halfDepth,
                    "Larger walking stride clips through the skirt at Y=" + height);
            }
        }
    }

    private static void checkInside(float[] pivot, List<double[][]> faces, String part) {
        for (double[][] face : faces) {
            double[] n = normal(face);
            double length = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            double distance = 0;
            for (int axis = 0; axis < 3; axis++) distance += n[axis] * (pivot[axis] - face[0][axis]);
            check(length > 0 && distance / length < -0.015, "Insufficient shoulder overlap: " + part);
        }
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] normal(double[][] face) {
        double[] n = new double[3];
        for (int i = 0; i < 3; i++) {
            int j = (i + 1) % 3, k = (i + 2) % 3;
            n[i] = (face[1][j] - face[0][j]) * (face[2][k] - face[0][k])
                - (face[1][k] - face[0][k]) * (face[2][j] - face[0][j]);
        }
        return n;
    }

    private static BufferedReader resource(String file) {
        InputStream stream = AyanamiRigCheck.class.getResourceAsStream("/assets/123technology/models/" + file);
        check(stream != null, "Missing asset " + file);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static void near(float expected, float value) {
        check(Math.abs(expected - value) < 0.001f, "Expected " + expected + ", got " + value);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
