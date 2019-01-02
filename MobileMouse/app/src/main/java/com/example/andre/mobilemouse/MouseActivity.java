package com.example.andre.mobilemouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.andre.mobilemouse.OnTouchListener.LeftClickListener;
import com.example.andre.mobilemouse.OnTouchListener.ScrollListener;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * https://developer.android.com/guide/topics/connectivity/bluetooth#java used as reference
 */
public class MouseActivity extends AppCompatActivity
        implements BluetoothDialogFragment.GetBluetoothDeviceListener, SensorEventListener {
    private static final String TAG = "MOUSE_ACTIVITY";

    private final int REQUEST_ENABLE_BT = 137;
    private final UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    private BluetoothDialogFragment mBTFragment;
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mSocket;
    private BluetoothWriter mBtWriter;

    private SensorManager mSensorManager;
    private Sensor mAccelerate;
    private long mTimestamp;
    private double lastX;
    private double lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        mBTFragment = BluetoothDialogFragment.newInstance();
        if ((mBTAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(this, getString(R.string.btnotavailable), Toast.LENGTH_SHORT).show();
            finish();
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerate = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mTimestamp = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBTFragment.show(getSupportFragmentManager(), "dialog");
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mBtWriter = new BluetoothWriter();

        View scroll = findViewById(R.id.mouseScroll);
        scroll.setOnTouchListener(new ScrollListener(mBtWriter));

        View leftClick = findViewById(R.id.leftClick);
        leftClick.setOnTouchListener(new LeftClickListener(mBtWriter));
    }

    /**
     * Handles motion of the phone as cursor movement.
     * @param event the SensorEvent of motion
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        if (mTimestamp != 0) {
            String s = "SWIPE:";
            double dt = 1;
            double dx = (int)(x * dt);
            double dy = (int)(y * dt) * -1;
            double x1 = dx + lastX;
            double y1 = dy + lastY;
            lastX = (dx == 0 ? 0 : x1);
            lastY = (dy == 0 ? 0 : y1);
            int xout = (int) (x1 * 100);
            int yout = (int) (y1 * 100);
            Log.i("HERPITY", x1 + " " + y1 + " " + dx + " " + dy + " " + dt);
            s += xout + ":";
            s += yout + "_";
            if (mBtWriter != null && (Math.abs(x1) >= 1 || Math.abs(y1) >= 1)) {
                mBtWriter.write(s);
            }
        }
        mTimestamp = (long)(event.timestamp * Math.pow(10, -9));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing...
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerate, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Handles Right Click
     * @param view
     */
    public void rightClick(View view) {
        try {
            String print = "BUTTON:3_";
            mBtWriter.write(print);
            TimeUnit.MILLISECONDS.sleep(10);
            print = "RELEASE:3_";
            mBtWriter.write(print);
        }
        catch (InterruptedException e) {

        }
    }

    @Override
    public void getBTDevice(BluetoothDevice device) {
        try {
            mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            mBtWriter.setOut(mSocket.getOutputStream());
        }
        catch(IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            Toast.makeText(this, getString(R.string.goodbye), Toast.LENGTH_SHORT);
            this.finish();
        }
    }
}
