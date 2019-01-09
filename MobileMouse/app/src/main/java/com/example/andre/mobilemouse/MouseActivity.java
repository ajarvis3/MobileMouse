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
import com.example.andre.mobilemouse.OnTouchListener.SwipeListener;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * https://developer.android.com/guide/topics/connectivity/bluetooth#java used as reference
 */
public class MouseActivity extends AppCompatActivity
        implements BluetoothDialogFragment.GetBluetoothDeviceListener {
    private static final String TAG = "MOUSE_ACTIVITY";

    private final int REQUEST_ENABLE_BT = 137;
    private final UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    private BluetoothDialogFragment mBTFragment;
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mSocket;
    private BluetoothWriter mBtWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        mBTFragment = BluetoothDialogFragment.newInstance();
        if ((mBTAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(this, getString(R.string.btnotavailable), Toast.LENGTH_SHORT).show();
            finish();
        }
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

        View swipe = findViewById(R.id.SwipeArea);
        swipe.setOnTouchListener(new SwipeListener(mBtWriter));
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
