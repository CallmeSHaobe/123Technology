package com.newmaa.othtech.common.render;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.newmaa.othtech.common.blocks.AyanamiAction;

/** Rigid, hierarchical bones in the standing OBJ's coordinate system. */
final class AyanamiRig {

    static final String HELD_ITEM_SOCKET = "right_hand";
    static final float LONGINUS_GRIP_PITCH = -75F;
    private static final float WALK_ARM_SWAY_DEGREES = 5;
    private static final float WALK_LEG_SWING_DEGREES = 17;
    private static final double WALK_CYCLE_BLOCKS = 0.70;

    private List<Bone> bones;
    private Map<String, Clip> clips;
    private transient Bone root;
    private transient Set<String> meshNames;

    static AyanamiRig read(Reader reader) {
        AyanamiRig rig = new Gson().fromJson(reader, AyanamiRig.class);
        require(rig != null, "Missing Ayanami rig");
        rig.bind();
        return rig;
    }

    Bone root() {
        return root;
    }

    boolean matchesMeshes(Set<String> names) {
        return meshNames.equals(names);
    }

    Clip clip(AyanamiAction action) {
        return clips.get(action.getId());
    }

    private void bind() {
        require(bones != null && !bones.isEmpty(), "Missing bones");
        Map<String, Bone> byName = new HashMap<>();
        meshNames = new HashSet<>();
        for (Bone bone : bones) {
            require(
                bone != null && bone.name != null && !bone.name.isEmpty() && !byName.containsKey(bone.name),
                "Invalid or duplicate bone");
            require(bone.pivot != null && bone.pivot.length == 3, "Invalid pivot for " + bone.name);
            for (float coordinate : bone.pivot) {
                require(Float.isFinite(coordinate), "Non-finite pivot for " + bone.name);
            }
            if (bone.pupilTravel != null) {
                float[] travel = bone.pupilTravel;
                require(
                    "head".equals(bone.parent) && travel.length == 3 && travel[0] > 0 && travel[1] > 0,
                    "Invalid pupil travel for " + bone.name);
                for (float value : travel) require(Float.isFinite(value), "Non-finite pupil travel for " + bone.name);
            }
            if (bone.pupilUpTravel != null) {
                require(
                    bone.pupilTravel != null && Float.isFinite(bone.pupilUpTravel) && bone.pupilUpTravel > 0,
                    "Invalid upward pupil travel for " + bone.name);
            }
            require(bone.meshes != null, "Missing mesh list for " + bone.name);
            for (String mesh : bone.meshes) {
                require(mesh != null && !mesh.isEmpty() && meshNames.add(mesh), "Invalid or duplicate mesh " + mesh);
            }
            if (bone.parent == null) {
                require(root == null, "Multiple root bones");
                root = bone;
            } else {
                Bone parent = byName.get(bone.parent);
                require(parent != null, "Parent must precede " + bone.name);
                parent.children.add(bone);
            }
            byName.put(bone.name, bone);
        }
        require(root != null && !meshNames.isEmpty(), "No root or mesh binding");
        Bone socket = byName.get(HELD_ITEM_SOCKET);
        require(
            socket != null && "right_arm".equals(socket.parent) && socket.meshes.length == 0,
            "Missing held item socket under the right arm");
        require(clips != null, "Missing clips");
        for (AyanamiAction action : AyanamiAction.values()) {
            Clip clip = clip(action);
            require(clip != null && clip.duration > 0 && clip.tracks != null, "Invalid clip " + action.getId());
            if (action == AyanamiAction.STANDING) require(clip.tracks.isEmpty(), "Standing must preserve the OBJ");
            for (Map.Entry<String, float[][]> track : clip.tracks.entrySet()) {
                require(byName.containsKey(track.getKey()), "Unknown animated bone " + track.getKey());
                validateFrames(track.getValue(), clip.duration);
            }
        }
    }

    private static void validateFrames(float[][] frames, int duration) {
        require(frames != null && frames.length >= 2, "A track needs start and end frames");
        float previous = -1;
        for (float[] frame : frames) {
            require(frame != null && frame.length == 4, "Expected [tick, x, y, z] keyframe");
            for (float value : frame) {
                require(Float.isFinite(value), "Non-finite keyframe");
            }
            require(frame[0] > previous, "Keyframe times must increase");
            previous = frame[0];
        }
        float[] first = frames[0];
        float[] last = frames[frames.length - 1];
        require(first[0] == 0 && last[0] == duration, "Track must span the whole clip");
        for (int axis = 1; axis <= 3; axis++) {
            require(first[axis] == last[axis], "Track must end at its starting rotation");
        }
    }

    static float walkingStride(float limbSwing, float limbSwingAmount) {
        // Vanilla integrates about four swing units per block. One cycle comprises two steps.
        double phase = limbSwing * (2 * Math.PI / (4 * WALK_CYCLE_BLOCKS));
        return (float) Math.cos(phase) * Math.max(0, Math.min(1, limbSwingAmount * 2.5F));
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    static final class Bone {

        String name;
        String parent;
        float[] pivot;
        String[] meshes;
        // [maximum local X, maximum downward Y, dZ/dY] for rigid planar pupils.
        float[] pupilTravel;
        Float pupilUpTravel;
        final transient List<Bone> children = new ArrayList<>();

        float maxPupilUpTravel() {
            return pupilUpTravel == null ? pupilTravel[1] : pupilUpTravel;
        }
    }

    /** Target eye position expressed in the coordinate system of the currently rendered bone. */
    static final class Gaze {

        private final double x;
        private final double y;
        private final double z;

        Gaze(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        static Gaze fromWorldOffset(double x, double y, double z, float modelYaw) {
            double angle = Math.toRadians(modelYaw);
            double cosine = Math.cos(angle), sine = Math.sin(angle);
            return new Gaze(cosine * x - sine * z, y, sine * x + cosine * z);
        }

        Gaze inBoneSpace(float[] pivot, float[] rotation) {
            // Undo the renderer's Z/Y/X matrix in that order on the target point.
            double x = this.x - pivot[0], y = this.y - pivot[1], z = this.z - pivot[2];
            if (rotation[2] != 0) {
                double angle = Math.toRadians(rotation[2]);
                double cosine = Math.cos(angle), sine = Math.sin(angle);
                double nextX = cosine * x + sine * y;
                y = cosine * y - sine * x;
                x = nextX;
            }
            if (rotation[1] != 0) {
                double angle = Math.toRadians(rotation[1]);
                double cosine = Math.cos(angle), sine = Math.sin(angle);
                double nextX = cosine * x - sine * z;
                z = sine * x + cosine * z;
                x = nextX;
            }
            if (rotation[0] != 0) {
                double angle = Math.toRadians(rotation[0]);
                double cosine = Math.cos(angle), sine = Math.sin(angle);
                double nextY = cosine * y + sine * z;
                z = cosine * z - sine * y;
                y = nextY;
            }
            return new Gaze(x + pivot[0], y + pivot[1], z + pivot[2]);
        }

        boolean pupilOffset(Bone eye, float[] result) {
            double dx = x - eye.pivot[0], dy = y - eye.pivot[1], dz = z - eye.pivot[2];
            float[] travel = eye.pupilTravel;
            double slope = travel[2];
            double planeScale = Math.sqrt(1 + slope * slope);
            double forward = (slope * dy - dz) / planeScale;
            if (forward <= 0.025) return false; // Behind the face or too close to resolve a look direction.
            double up = (dy + slope * dz) / planeScale;
            double horizontal = dx / (forward * 0.55);
            double vertical = up / (forward * 0.40);
            double limit = Math.max(1, Math.hypot(horizontal, vertical));
            result[0] = (float) (travel[0] * horizontal / limit);
            float verticalTravel = vertical < 0 ? travel[1] : eye.maxPupilUpTravel();
            result[1] = (float) (verticalTravel * vertical / limit);
            result[2] = travel[2] * result[1];
            return result[0] != 0 || result[1] != 0;
        }
    }

    static final class Clip {

        private int duration;
        private Map<String, float[][]> tracks;

        float loopTime(long elapsedTicks, float partialTick) {
            if (elapsedTicks < 0) return 0;
            return (elapsedTicks % duration + partialTick) % duration;
        }

        float oneShotTime(long elapsedTicks, float partialTick) {
            return elapsedTicks < 0 ? 0 : Math.min(duration, elapsedTicks + partialTick);
        }

        /** Explicit keyframes take priority; unanimated bones can still track gaze and locomotion. */
        void samplePose(String bone, float tick, float stride, float aimYaw, float aimPitch, float[] rotation) {
            sample(bone, tick, rotation);
            if (tracks.containsKey(bone)) return;
            switch (bone) {
                case "right_leg":
                    rotation[0] = stride * WALK_LEG_SWING_DEGREES;
                    break;
                case "left_leg":
                    rotation[0] = -stride * WALK_LEG_SWING_DEGREES;
                    break;
                case "right_arm":
                    rotation[0] = -stride * WALK_ARM_SWAY_DEGREES;
                    break;
                case "left_arm":
                    rotation[0] = stride * WALK_ARM_SWAY_DEGREES;
                    break;
                case "head":
                    rotation[0] = aimPitch;
                    rotation[1] = aimYaw;
                    break;
                default:
                    break;
            }
        }

        void sample(String bone, float tick, float[] rotation) {
            float[][] frames = tracks.get(bone);
            if (frames == null) {
                rotation[0] = rotation[1] = rotation[2] = 0;
                return;
            }
            int i = 1;
            while (i < frames.length - 1 && tick > frames[i][0]) i++;
            float[] from = frames[i - 1];
            float[] to = frames[i];
            float t = Math.max(0, Math.min(1, (tick - from[0]) / (to[0] - from[0])));
            t = t * t * (3 - 2 * t); // Smoothstep between authored rotations.
            for (int axis = 0; axis < 3; axis++) {
                rotation[axis] = from[axis + 1] + (to[axis + 1] - from[axis + 1]) * t;
            }
        }
    }
}
