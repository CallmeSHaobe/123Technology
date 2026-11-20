package com.newmaa.othtech.common.entity;

/** Standalone numerical regression: javac FlightDynamics.java FlightDynamicsCheck.java; java ...FlightDynamicsCheck. */
public final class FlightDynamicsCheck {

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        double[][] targets = { { 0, 64, 1000 }, { 0, 64, 40 }, { 0, 64, -200 }, { 300, 180, 300 }, { 300, 10, 300 },
            { 1000, 64, 0 }, { 0, 64, 10 }, { 0, 200, 100 }, { 0, 10, -40 }, { 0, 64, 0 }, { 0, 245, 100 },
            { 0, 1, 500 } };
        for (int speedKmh : new int[] { 50, 100, 200, 300 }) {
            double q = (speedKmh / 72.0) / 1.4;
            for (double launchY : new double[] { 64, 245 }) {
                for (int quadrant = 0; quadrant < 4; quadrant++) {
                    double yaw = quadrant * 90, a = Math.toRadians(yaw);
                    for (double[] target : targets) {
                        double tx = target[0] * Math.cos(a) - target[2] * Math.sin(a);
                        double tz = target[0] * Math.sin(a) + target[2] * Math.cos(a);
                        FlightDynamics.State s = new FlightDynamics.State();
                        s.y = launchY;
                        s.cruiseHeight = Math.min(250, Math.max(launchY + 16, target[1] + 24));
                        FlightDynamics.launch(s, yaw, speedKmh);
                        require(
                            Math.abs(Math.sqrt(s.vx * s.vx + s.vy * s.vy + s.vz * s.vz) - speedKmh / 72.0) < 1e-9,
                            "launch speed in km/h / 72");
                        require(
                            s.vx * -Math.sin(a) + s.vz * Math.cos(a) > speedKmh / 72.0 * 0.97 && s.vy > 0,
                            "forward launch");
                        double closest = Double.MAX_VALUE, minY = launchY, maxY = launchY, maxSpeed = 0,
                            minSpeed = Double.MAX_VALUE, maxTurn = 0;
                        int hit = -1;
                        for (int i = 0; i < 12000; i++) {
                            double x = s.x, y = s.y, z = s.z, bank = s.bank, pitch = s.pitch;
                            double heading = Math.toDegrees(Math.atan2(-s.vx, s.vz));
                            FlightDynamics.step(s, tx, target[1], tz);
                            double d = FlightDynamics.segmentDistance(x, y, z, s.x, s.y, s.z, tx, target[1], tz);
                            closest = Math.min(closest, d);
                            minY = Math.min(minY, s.y);
                            maxY = Math.max(maxY, s.y);
                            double speed = Math.sqrt(s.vx * s.vx + s.vy * s.vy + s.vz * s.vz);
                            require(!Double.isNaN(speed) && !Double.isInfinite(speed), "finite speed");
                            require(Math.abs(s.bank - bank) <= 3 * q + 0.000001, "bank slew");
                            require(Math.abs(s.pitch - pitch) <= 2 * q + 0.000001, "pitch slew");
                            require(Math.abs(s.bank) <= 55.000001, "bank limit");
                            require(s.y >= 0 && s.y <= 255, "world altitude: " + s.y);
                            require(i * q >= 30 || s.phase == FlightDynamics.LAUNCH, "time-scaled rail-clear phase");
                            maxSpeed = Math.max(maxSpeed, speed);
                            minSpeed = Math.min(minSpeed, speed);
                            double turn = Math.toDegrees(Math.atan2(-s.vx, s.vz)) - heading;
                            turn -= Math.floor((turn + 180) / 360) * 360;
                            maxTurn = Math.max(maxTurn, Math.abs(turn));
                            if (s.phase == FlightDynamics.DIVE && d <= 0.9) {
                                hit = i + 1;
                                break;
                            }
                        }
                        System.out.printf(
                            "speedKmh=" + speedKmh
                                + " launchY=%.0f yaw=%.0f target=(%.0f,%.0f,%.0f) hit=%d distance=%.3f altitude=[%.2f,%.2f] speed=[%.3f,%.3f] maxTurn=%.3f%n",
                            launchY,
                            yaw,
                            tx,
                            target[1],
                            tz,
                            hit,
                            closest,
                            minY,
                            maxY,
                            minSpeed,
                            maxSpeed,
                            maxTurn);
                        require(hit > 0, "target did not converge");
                        require(minSpeed > 1.0 * q && maxSpeed < 1.8 * q, "fixed-wing speed, never hover");
                        require(maxTurn < 3 * q, "finite turn rate");
                    }
                }
            }
        }
        System.out.println(
            "PASS: 384 cases (50/100/200/300 km/h x 96 rotated target/launch-height cases); force integration, launch, convergence, speed and attitude bounds.");
    }
}
