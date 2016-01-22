package com.singh.zoodu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;
import java.util.logging.Handler;

public class HomeActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "HomeActivity";
    private static final boolean D = true;

    // Bluetooth Stuff
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> deviceArrayAdapter;
    private ListView deviceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(D) {
            Log.i(TAG, "+++ ON CREATE +++");
        }
        super.onCreate(savedInstanceState);

        initializeGUI();
        checkBluetooth();
    }

    private void initializeGUI() {
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        deviceArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        deviceListView = (ListView) findViewById(R.id.deviceView);
        deviceListView.setAdapter(deviceArrayAdapter);
    }

    public void checkBluetooth() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "ERR: No Bluetooth Adapter");
        } else {
            Log.d(TAG, "Found Bluetooth Adapter");
            if (!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "Enabling Bluetooth Adapter");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            } else {
                Log.d(TAG, "Bluetooth Already Enabled");
                getDeviceList();
            }
        }
    }


    private boolean isScanning;
    private Handler btHandler;
    private void getDeviceList() {
        Set<BluetoothDevice> nearbyDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (nearbyDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : nearbyDevices) {
                // Add the name and address to an array adapter to show in a ListView
                deviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Log.e(TAG, "No Bluetooth Devices found");
            deviceArrayAdapter.add("No devices found");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Log.d(TAG, "Bluetooth Enabled");
            getDeviceList();
        } else {
            Log.e(TAG, "Failed turning on Bluetooth");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(D) {
            Log.e(TAG, "--- ON DESTROY ---");
        }
    }
}
