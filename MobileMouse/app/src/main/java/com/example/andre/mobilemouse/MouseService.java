package com.example.andre.mobilemouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Based on https://github.com/googlesamples/android-BluetoothChat/blob/master/Application/src/main/java/com/example/android/bluetoothchat/BluetoothChatService.java
 */
public class MouseService {
    private static final String TAG = "MOUSE_SERVICE";
    private static final UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    private final BluetoothAdapter mAdapter;
    private int mState;
    private int mNewState;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    /**
     * Constructor
     * @param context the Context for the activity
     */
    public MouseService(Context context) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
    }

    /**
     * Starts the connection between this device and the server
     */
    public void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(null); // Uhhhhh I'll get back to you on that chief
    }

    /**
     * Starts thread to make a new connection
     * @param device the BluetoothDevice the client wants to connect to
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

    }

    /**
     * Starts a connected thread to manage a connection
     * @param socket the BluetoothSocket used in the connection
     * @param device the Bluetooth device that is connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        /*
         * I think we need the handler down here
         */
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mState = STATE_NONE;
    }

    /**
     * Writes to the output stream
     * @param out
     */
    public void write(byte[] out) {
        ConnectedThread temp;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            temp = mConnectedThread;
        }
        temp.write(out);
    }

    /**
     * Indicates connection failed
     */
    private void connectionFailed() {
        mState = STATE_NONE;

        MouseService.this.start();
    }

    /**
     * The thread used to make a connection
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        /**
         * Constructor for the thread
         * @param device the BluetoothDevice that the client wants to connect to
         */
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        /**
         * Runs the thread
         */
        public void run() {
            setName("ConnectThread");

            mAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            }
            catch (IOException e) {
                try {
                    mmSocket.close();
                }
                catch (IOException e2) {
                    Log.e(TAG, e2.toString());
                }
                connectionFailed();
                return;
            }

            synchronized (MouseService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        /**
         * Cancels the connection
         */
        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Thread used by the service when connected
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;

        /**
         * Constructor
         * @param socket the BluetoothSocket used in the conncection
         */
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;

            try {
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }

            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        /**
         * Run the thread... don't think it is overly necessary?
         */
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // Do I really need anything?
        }

        /**
         * Writes to the output stream
         * @param bytes the byte array to write
         */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        /**
         * Cancels connection
         */
        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

}
