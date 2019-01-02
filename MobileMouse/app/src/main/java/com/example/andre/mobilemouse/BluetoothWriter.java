package com.example.andre.mobilemouse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Holds the OutputStream for the BluetoothConnection
 */
public class BluetoothWriter {
    private OutputStream mOut;

    /**
     * Default constructor.
     */
    public BluetoothWriter() {
        mOut = System.out;
    }

    /**
     * User-defined constructor.
     * Use this one.
     * @param out the OutputStream used by the Bluetooth Socket.
     */
    public BluetoothWriter(OutputStream out) {
        mOut = out;
    }

    /**
     * Sets the outputstream
     * @param out the OutputStream that will be used
     */
    public void setOut(OutputStream out) {
        mOut = out;
    }

    /**
     * Writes to the output stream.
     * @param s the String to write to the Bluetooth output stream.
     */
    public void write(String s) {
        try {
            mOut.write(s.getBytes());
            mOut.flush();
        }
        catch (IOException e) {

        }
    }
}
