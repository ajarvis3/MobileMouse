package com.example.andre.mobilemouse.OnTouchListener;

import android.view.MotionEvent;
import android.view.View;

import com.example.andre.mobilemouse.BluetoothWriter;

/**
 * Handles left clicks in the mouse activity
 */
public class LeftClickListener implements View.OnTouchListener {
    private BluetoothWriter mWrite;

    /**
     * Default Constructor
     */
    public LeftClickListener() {
        mWrite = new BluetoothWriter();
    }

    /**
     * User-defined constructor
     * @param bt the BluetoothWriter to be used by this
     */
    public LeftClickListener(BluetoothWriter bt) {
        mWrite = bt;
    }

    /**
     * On touch listener for left click.
     * @param v the View being touched
     * @param event the MotionEvent of touch
     * @return a boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            String s = "BUTTON:1_";
            mWrite.write(s);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            String s = "RELEASE:1_";
            mWrite.write(s);
        }
        return true;
    }
}
