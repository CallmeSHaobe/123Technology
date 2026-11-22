package com.newmaa.othtech.common.entity;

/** Tick-unit, Minecraft-independent fixed-wing model. Position is the physical body's base. */
public final class FlightDynamics {

    public static final int ASCEND = 0, CRUISE = 1, DIVE = 2, LAUNCH = 3, APPROACH = 4;
    public static final double GRAVITY = 0.035;

    public static final class State {

        public double x, y, z, vx, vy, vz;
        /** Degrees; Minecraft yaw, pitch positive nose down, bank positive left turn. */
        public double yaw, pitch, bank;
        public int phase = LAUNCH, age;
        /** Zero preserves the original power train for pre-speed-setting saved entities. */
        public int speedKmh;
        public double cruiseHeight, approachX, approachZ;
    }

    private FlightDynamics() {}

    public static void launch(State s, double yaw) {
        s.speedKmh = 0;
        s.yaw = yaw;
        s.pitch = -12;
        s.bank = 0;
        s.vx = -Math.sin(rad(yaw)) * Math.cos(rad(12)) * 1.2;
        s.vy = Math.sin(rad(12)) * 1.2;
        s.vz = Math.cos(rad(yaw)) * Math.cos(rad(12)) * 1.2;
        s.phase = LAUNCH;
        s.age = 0;
    }

    public static int sanitizeSpeedKmh(int speedKmh) {
        return Math.max(50, Math.min(300, speedKmh));
    }

    public static void launch(State s, double yaw, int speedKmh) {
        launch(s, yaw);
        s.speedKmh = sanitizeSpeedKmh(speedKmh);
        double scale = (s.speedKmh / 72.0) / 1.2;
        s.vx *= scale;
        s.vy *= scale;
        s.vz *= scale;
    }

    /**
     * Time-similar fixed-wing dynamics: velocity scales by q and forces by q squared.
     * Thus low-speed wings still support weight and high-speed approaches retain enough
     * turning authority for the same spatial path. Substeps keep the high-speed solver stable;
     * they integrate forces, never replace velocity with a target-point direction.
     */
    public static void step(State s, double tx, double ty, double tz) {
        double q = s.speedKmh == 0 ? 1 : (s.speedKmh / 72.0) / 1.4;
        int steps = s.speedKmh == 0 ? 1 : (int) Math.ceil(q * 2);
        double dt = q / steps;
        s.vx /= q;
        s.vy /= q;
        s.vz /= q;
        for (int i = 0; i < steps; i++) {
            double x = s.x, y = s.y, z = s.z;
            integrate(s, tx, ty, tz, dt, q);
            // Do not let a later substep turn an already-hit dive into a go-around.
            if (s.phase == DIVE && segmentDistance(x, y, z, s.x, s.y, s.z, tx, ty, tz) <= 0.9) {
                double dx = s.x - x, dy = s.y - y, dz = s.z - z;
                double n = dx * dx + dy * dy + dz * dz;
                double t = n > 0 ? clamp(((tx - x) * dx + (ty - y) * dy + (tz - z) * dz) / n, 0, 1) : 0;
                s.x = x + dx * t;
                s.y = y + dy * t;
                s.z = z + dz * t;
                break;
            }
        }
        s.vx *= q;
        s.vy *= q;
        s.vz *= q;
        s.age++;
    }

    private static void integrate(State s, double tx, double ty, double tz, double dt, double q) {
        double dx = tx - s.x, dz = tz - s.z;
        double range = Math.sqrt(dx * dx + dz * dz);
        double v = Math.sqrt(s.vx * s.vx + s.vy * s.vy + s.vz * s.vz);
        double horizontal = Math.sqrt(s.vx * s.vx + s.vz * s.vz);
        double heading = Math.toDegrees(Math.atan2(-s.vx, s.vz));
        double gamma = Math.atan2(s.vy, horizontal);
        double targetHeading = Math.toDegrees(Math.atan2(-dx, dz));
        double headingError = wrap(targetHeading - heading);
        if (s.phase == LAUNCH && s.age * q >= 30) s.phase = ASCEND;
        if (s.phase == ASCEND && s.y >= s.cruiseHeight - 3) s.phase = CRUISE;
        // Establish a long final approach instead of orbiting a target inside the turn radius.
        if ((s.phase == CRUISE && range < 110 && Math.abs(headingError) > 12)
            || (s.phase == DIVE && range < 25 && Math.abs(headingError) > 40)) {
            double awayX = s.x - tx, awayZ = s.z - tz;
            double awayLength = Math.max(1, range);
            double approachDistance = Math.max(200, Math.abs(s.y - ty) * 3);
            s.approachX = tx + awayX / awayLength * approachDistance;
            s.approachZ = tz + awayZ / awayLength * approachDistance;
            s.phase = APPROACH;
        }
        if (s.phase == APPROACH) {
            double ax = s.approachX - s.x, az = s.approachZ - s.z;
            if (ax * ax + az * az < 50 * 50) s.phase = CRUISE;
            else headingError = wrap(Math.toDegrees(Math.atan2(-ax, az)) - heading);
        }
        // Do not pitch down while still turning away from a close/behind target.
        if (s.phase == CRUISE && Math.abs(headingError) < 12
            && range > 50
            && range < Math.max(150, Math.abs(s.y - ty) * 2.5)) s.phase = DIVE;
        double wantedGamma;
        double wantedBank;
        if (s.phase == LAUNCH) {
            // Keep the catapult's initial 12-degree velocity, but unload the wing near the ceiling.
            // This changes acceleration, not velocity, and leaves horizontal rail clearance intact.
            wantedGamma = Math.min(rad(12), (250 - s.y) * 0.025);
            wantedBank = 0;
        } else {
            wantedBank = clamp(headingError * 1.8, -55, 55);
            if (s.phase == DIVE) {
                double los = Math.atan2(ty - s.y, Math.max(0.01, range));
                wantedGamma = clamp(los + 0.65 * (los - gamma), rad(-72), rad(55));
            } else {
                wantedGamma = clamp((s.cruiseHeight - s.y) * 0.025, rad(-15), rad(22));
            }
        }
        s.bank += clamp(wantedBank - s.bank, -3 * dt, 3 * dt);
        s.pitch += clamp(-Math.toDegrees(wantedGamma) - s.pitch, -2 * dt, 2 * dt);
        // The fuselage follows airflow with a bounded yaw rate (no instantaneous nose snapping).
        s.yaw += clamp(wrap(heading - s.yaw), -3 * dt, 3 * dt);
        double inv = 1 / Math.max(0.05, v);
        double ux = s.vx * inv, uy = s.vy * inv, uz = s.vz * inv;
        // Wing-normal is perpendicular to airflow: lift does not create free forward energy.
        double upx = -ux * uy, upy = 1 - uy * uy, upz = -uz * uy;
        double upLength = Math.sqrt(upx * upx + upy * upy + upz * upz);
        upx /= Math.max(0.001, upLength);
        upy /= Math.max(0.001, upLength);
        upz /= Math.max(0.001, upLength);
        double leftx = -uz / Math.max(0.001, Math.sqrt(ux * ux + uz * uz));
        double leftz = ux / Math.max(0.001, Math.sqrt(ux * ux + uz * uz));
        double bank = rad(s.bank);
        double lift = clamp(
            (GRAVITY * Math.cos(gamma) + (wantedGamma - gamma) * 0.16) / Math.max(0.4, Math.cos(bank)),
            0,
            0.11 * v * v);
        double drag = 0.012 * v * v + 0.08 * lift * lift / Math.max(0.3, v * v);
        double thrust = clamp(0.012 * 1.4 * 1.4 + (1.4 - v) * 0.065 + GRAVITY * Math.sin(gamma), 0.008, 0.075);
        double nosePitch = rad(-s.pitch), noseYaw = rad(s.yaw);
        s.vx += (-Math.sin(noseYaw) * Math.cos(nosePitch) * thrust - ux * drag
            + (upx * Math.cos(bank) + leftx * Math.sin(bank)) * lift) * dt;
        s.vy += (Math.sin(nosePitch) * thrust - uy * drag + upy * Math.cos(bank) * lift - GRAVITY) * dt;
        s.vz += (Math.cos(noseYaw) * Math.cos(nosePitch) * thrust - uz * drag
            + (upz * Math.cos(bank) + leftz * Math.sin(bank)) * lift) * dt;
        s.x += s.vx * dt;
        s.y += s.vy * dt;
        s.z += s.vz * dt;
    }

    /** Swept target proximity, including a target crossed between ticks. */
    public static double segmentDistance(double ax, double ay, double az, double bx, double by, double bz, double tx,
        double ty, double tz) {
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double n = dx * dx + dy * dy + dz * dz;
        double t = n > 0 ? clamp(((tx - ax) * dx + (ty - ay) * dy + (tz - az) * dz) / n, 0, 1) : 0;
        dx = ax + t * dx - tx;
        dy = ay + t * dy - ty;
        dz = az + t * dz - tz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static double rad(double degrees) {
        return degrees * Math.PI / 180;
    }

    private static double clamp(double x, double low, double high) {
        return Math.max(low, Math.min(high, x));
    }

    private static double wrap(double degrees) {
        return degrees - Math.floor((degrees + 180) / 360) * 360;
    }
}
