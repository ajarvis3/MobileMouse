package com.example.andre.mobilemouse;


import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Set;


/**
 * A DialogFragment that gets the BluetoothDevice to be used by this app.
 * The Device must be paired.
 */
public class BluetoothDialogFragment extends DialogFragment {

    public static BluetoothDialogFragment newInstance() {
        BluetoothDialogFragment bdf = new BluetoothDialogFragment();

        return bdf;
    }

    /**
     * The constructor for this fragment
     */
    public BluetoothDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the AlertDialog for this fragment
     * @param savedInstanceState saved stuff
     * @return the AlertDialog for this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Set<BluetoothDevice> pairedDevices =
                BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        builder.setItems(getNameArray(pairedDevices), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), getString(R.string.connecting), Toast.LENGTH_SHORT);
                GetBluetoothDeviceListener listen = (GetBluetoothDeviceListener)getActivity();
                listen.getBTDevice(getBTDevice(pairedDevices, which));
            }
        });
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_dialog, container, false);
    }

    /**
     * Gets the names of each device and returns it as an array of String objects
     * @param devices the Set of paired BluetoothDevice objects
     * @return the String array of names
     */
    private String[] getNameArray(Set<BluetoothDevice> devices) {
        String[] deviceNames = new String[devices.size()];
        int i = 0;
        for (BluetoothDevice bd : devices) {
            deviceNames[i++] = bd.getName();
        }
        return deviceNames;
    }

    /**
     * Gets the bluetooth device at the index specified
     * @param devices the Set of BluetoothDevices that are paired
     * @param num the int representing the index to look up
     * @return the Bluetooth device at the index, null if out of bounds
     */
    private BluetoothDevice getBTDevice(Set<BluetoothDevice> devices, int num) {
        int i = 0;
        for (BluetoothDevice bd : devices) {
            if (i == num) {
                return bd;
            }
            i = i + 1;
        }
        return null;
    }

    /**
     * An interface used to get information out of the dialog fragment
     */
    public interface GetBluetoothDeviceListener {
        void getBTDevice(BluetoothDevice device);
    }

}
