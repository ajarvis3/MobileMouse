package com.example.andre.mobilemouse.OnTouchListener;

import android.view.MotionEvent;
import android.view.View;

import com.example.andre.mobilemouse.BluetoothWriter;

/**
 * Listener for Scroll view in mouse activity
 */
public class ScrollListener implements View.OnTouchListener {
    private double yScrollLast;
    private BluetoothWriter mWriter;

    /**
     * Default Constructor
     */
    public ScrollListener() {
        yScrollLast = 0;
        mWriter = new BluetoothWriter();
    }

    /**
     * User-defined Constructor
     * @param bt the BluetoothWriter used in this method
     */
    public ScrollListener(BluetoothWriter bt) {
        yScrollLast = 0;
        mWriter = bt;
    }

    /**
     * On Touch method to handle scroll
     * @param v the View being touched
     * @param event the MotionEvent of the touch
     * @return a boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        double y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yScrollLast = y;
                break;
            case MotionEvent.ACTION_MOVE:
                String s = "SCROLL:";
                int scroll = (int)(y - yScrollLast) % 20;
                s += scroll;
                s += "_";
                yScrollLast = y;
                if (scroll != 0)
                    mWriter.write(s);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
