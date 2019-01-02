package com.example.andre.mobilemouse.OnTouchListener;

import android.view.MotionEvent;
import android.view.View;

import com.example.andre.mobilemouse.BluetoothWriter;

/**
 * A listener for swipes.
 * Should not be used in the current version.
 */
public class SwipeListener implements View.OnTouchListener {
    private BluetoothWriter mWrite;
    private double xSwipeLast;
    private double ySwipeLast;

    /**
     * User-defined constructor
     * @param bw the BluetoothWrtiter used for the connection
     */
    public SwipeListener(BluetoothWriter bw) {
        mWrite = bw;
    }

    /**
     * The onTouch Method for this listener
     * @param v the View being touched
     * @param event the MotionEvent of the touch
     * @return a boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        double x = event.getX();
        double y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ySwipeLast = y;
            xSwipeLast = x;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            String s = "SWIPE:";
            int dx = (int)(x - xSwipeLast);
            int dy = (int)(y - ySwipeLast);
            xSwipeLast = x;
            ySwipeLast = y;
            s += dx + ":";
            s += dy + "_";
            if (Math.abs(dx) >= 1 || Math.abs(dy) >= 1)
                mWrite.write(s);
        }
        return true;
    }
}
