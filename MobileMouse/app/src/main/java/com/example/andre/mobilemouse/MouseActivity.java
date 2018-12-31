package com.example.andre.mobilemouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * https://developer.android.com/guide/topics/connectivity/bluetooth#java used as reference
 */
public class MouseActivity extends AppCompatActivity implements BluetoothDialogFragment.GetBluetoothDeviceListener {
    private static final String TAG = "MOUSEACTIVITY";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDialogFragment mBTFragment;
    private BluetoothSocket mSocket;
    private final int REQUEST_ENABLE_BT = 137;
    private final UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");
    private double yScrollLast;
    private double xSwipeLast;
    private double ySwipeLast;
    private SwipeCoordinate mSwipeCoor;

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
        View scroll = findViewById(R.id.mouseScroll);
        scroll.setOnTouchListener(new View.OnTouchListener() {

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
                            write(s);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
        View swipe = findViewById(R.id.SwipeArea);
        swipe.setOnTouchListener(new View.OnTouchListener() {

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
                        write(s);
                }
                return true;
            }
        });
        View leftClick = findViewById(R.id.leftClick);
        leftClick.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String s = "BUTTON:1_";
                    write(s);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    String s = "RELEASE:1_";
                    write(s);
                }
                return true;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            Toast.makeText(this, getString(R.string.goodbye), Toast.LENGTH_SHORT);
            this.finish();
        }
    }

    @Override
    public void getBTDevice(BluetoothDevice device) {
        try {
            mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
        }
        catch(IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void write(String write) {
        try {
            OutputStream out = mSocket.getOutputStream();
            out.write(write.getBytes());
            out.flush();
        }
        catch (IOException e) {

        }
    }

    /**
     * Handles Right Click
     * @param view
     */
    public void rightClick(View view) {
        try {
            String print = "BUTTON:3_";
            write(print);
            TimeUnit.MILLISECONDS.sleep(10);
            print = "RELEASE:3_";
            write(print);
        }
        catch (InterruptedException e) {

        }
    }
}
