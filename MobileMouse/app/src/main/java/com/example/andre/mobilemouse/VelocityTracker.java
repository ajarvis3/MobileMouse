package com.example.andre.mobilemouse;

import android.util.Log;

/**
 * Class that helps to track the velocity based on accelerometer measurements.
 *
 * Uses acceleration and change in time to attempt to estimate velocity.
 * Beyond t1 is just further example of  idea, not expressed in the formulas given below.
 *       q
 *      /| \
 *     / |__\
 *   x___y   |
 *  /|   |   |
 * /_|___|___|
 *   t0   t1
 * dt = t1 - t0
 * z0 = the line from t0 to x = sqrt(acceleration^2 - dt^2) if sign acceleration same as sumAccel
 * z1 = the line from t1  to q = z0 + sqrt(sumAccel - totalTime) if signs are different
 *
 * Area of rectangle = z0 * dt (same sign) or sumAccel
 * Area of triangle =
 *
 * Currently there may be some error where it crosses the x-axis... Hopefully negated since it
 * should always be equal to zero prior to making the switch.
 */
public class VelocityTracker {
    private static int MAX = 200;

    private long mLastTime;
    private double mLastZ;

    private long mTotalTime;
    private double mSumVelo;
    private double mSumAccel;
    private double mRecentVelo;

    /**
     * Default Constructor.
     * May need to change how last time is tracked.
     */
    public VelocityTracker() {
        reset();
    }

    /**
     * Resets the values of the member values
     */
    public void reset() {
        mLastZ = 0;
        mSumVelo = 0;
        mSumAccel = 0;
        mTotalTime = 0;
        mLastTime = System.currentTimeMillis() / 10000;
    }

    /**
     * Gets the area of a triangle.
     * Uses pythagorean theorem to find the height.
     * @param acceleration the double representing the value of acceleration in this dimension
     * @param dt the double representing change in time.
     * @return the double representing the area of the triangle
     */
    private double triangle(double acceleration, double dt) {
        double area = 0;
        double pyth = Math.pow(acceleration, 2) - Math.pow(dt, 2);
        if (pyth <= 0)
            return 0;
        double height = Math.sqrt(pyth);
        area = .5 * height * dt;
        if (mSumAccel < 0) {
            area = area * -1;
        }
        Log.i("OUTMOUSE", "AREA: " + area);
        return area;
    }

    /**
     * Checks if the acceleration has the same sign as the sum of accelerations.
     * If sum is zero, it will always return true.
     * @param acceleration the double representing current acceleration
     * @return the boolean representing whether the signs are the same.
     */
    private boolean sameSign(double acceleration) {
        if (mSumAccel == 0) {
            return true;
        }
        if (mSumAccel > 0 && acceleration > 0 || mSumAccel < 0 && acceleration < 0) {
            return true;
        }
        return false;
    }

    /**
     * Gets the area of a rectangle.
     * @param acceleration the double representing the value of acceleration in this dimension
     * @param dt the double representing change in time.
     * @return the double representing the area of the rectangle.
     */
    private double rectangle(double acceleration, double dt){
        double area = 0;
        Log.i("OUTMOUSE", "DT: " + dt + " A: " + acceleration);
        if (sameSign(acceleration)) {
            area = mLastZ * dt;
        }
        else {
            double pyth = Math.pow(mSumAccel, 2) - Math.pow(dt, 2);
            if (pyth <= 0)
                return 0;
            double height = Math.sqrt(pyth);
            area = height * dt;
        }
        if (mSumAccel < 0) {
            area = area * -1;
        }
        Log.i("OUTMOUSE", "R AREA: " + area);
        return area;
    }

    /**
     * Updates the values of the velocity tracker
     * @param accel the double representing acceleration in this dimension
     * @param time the long representing the timestamp
     */
    public void update(double accel, long time) {
        time = time / 10000000;
        long dt = time - mLastTime;
        mSumAccel = mSumAccel + accel;

        Log.i("OUTMOUSE", "VELO" + mSumVelo);
        mRecentVelo = triangle(accel, dt) + rectangle(accel, dt);
        mSumVelo = mSumVelo + mRecentVelo;

        mLastTime = time;
        mTotalTime = mTotalTime + dt;
    }

    /**
     * Gets the current integer velocity as a String
     * @return the String representing the integer velocity
     */
    public String getOutputVelo(int multiplier) {
        double ret = mRecentVelo;
        if (ret > MAX) {
            ret = MAX;
        }
        else if (ret < -1 * MAX) {
            ret = -1 * MAX;
        }
        return Integer.toString((int)ret);
    }
}
