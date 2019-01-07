package com.example.andre.mobilemouse;

import android.hardware.SensorEvent;
import android.util.Log;

public class AccelerometerHandler {
    private static int MULTIPLIER = 25;
    private static double IGNORE_ACCEL = .5;

    private VelocityTracker mXVelo;
    private VelocityTracker mYVelo;

    public AccelerometerHandler() {
        mXVelo = new VelocityTracker();
        mYVelo = new VelocityTracker();
    }

    public String handleSensor(SensorEvent e) {
        String res = "SWIPE:";
        double ax = e.values[0];
        double ay = e.values[1] * -1;
        long time = e.timestamp;
        mXVelo.update(ax, time);
        mYVelo.update(ay, time);
        if (Math.abs(ax) < IGNORE_ACCEL) {
            mXVelo.reset();
        }
        if (Math.abs(ay) < IGNORE_ACCEL) {
            mYVelo.reset();
        }
        String xout = mXVelo.getOutputVelo(MULTIPLIER);
        String yout = mYVelo.getOutputVelo(MULTIPLIER);
        res = res + xout + ":" + yout + "_";
        return res;
    }
}
