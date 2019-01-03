package com.example.andre.mobilemouse;

import android.hardware.SensorEvent;
import android.util.Log;

public class AccelerometerHandler {
    private static int MULTIPLIER = 50;

    private double mDisplaceX;
    private double mDisplaceY;
    private double mLastDx;
    private double mLastDy;
    private double mVx0;
    private double mVy0;

    public AccelerometerHandler() {
        mDisplaceX = 0;
        mDisplaceX = 0;
        mLastDx = 0;
        mLastDy = 0;
        mVx0 = 0;
        mVy0 = 0;
    }

    public String handleSensor(SensorEvent e) {
        String res = "SWIPE:";
        double ax = e.values[0];
        double ay = e.values[1] * -1;
        double vx1 = ax - mVx0;
        double vy1 = ay - mVy0;
        if (Math.abs(ax) > 0.5) {
            mDisplaceX = mDisplaceX + vx1;
            mVx0 = vx1;
            if (Math.abs(mDisplaceX) < Math.abs(mLastDx)) {
                vx1 = 0;
            }
        }
        else if (Math.abs(ax) < .4) {
            mDisplaceX = 0;
            mVx0 = 0;
        }
        if (Math.abs(ay) > 0.5) {
            mDisplaceY = mDisplaceY + vy1;
            mVy0 = vy1;
            if (Math.abs(mDisplaceY) < Math.abs(mLastDy)) {
                vy1 = 0;
            }
        }
        else if (Math.abs(ax) < .4) {
            mDisplaceY = 0;
            mVy0 = 0;
        }
        Log.i("AccelerometerHandler", "ax: " + ax + " vx0: " + mVx0 + " vx1: " + vx1 +
                " dx: " + mDisplaceX + " ay: " + ay + " vy0: " + mVy0 + " vy1: " + vy1 + " dy: " + mDisplaceY);
        int xout = (int) (vx1 * MULTIPLIER);
        int yout = (int) (vy1 * MULTIPLIER);
        mLastDx = mDisplaceX;
        mLastDy = mDisplaceY;
        res = res + xout + ":" + yout + "_";
        return res;
    }
}
