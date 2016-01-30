package com.singh.zoodu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "HomeActivity";
    private static final boolean D = true;

    // Bluetooth Stuff
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> deviceArrayAdapter;
    private ListView deviceListView;
    private boolean isEnabled;
    private boolean isScanning;
    private HashMap<String, String> deviceHash = new HashMap<>();
    private static final long SCAN_PERIOD = 10000;

    // Data
    private JSONArray animalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(D) { Log.i(TAG, "+++ ON CREATE +++"); }
        super.onCreate(savedInstanceState);

        animalData = loadJSONFromAsset();

        initializeGUI();
        checkBluetooth();
    }

    private void initializeGUI() {
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        deviceListView = (ListView) findViewById(R.id.deviceView);
        deviceListView.setAdapter(deviceArrayAdapter);
    }

    public void checkBluetooth() {
        if (bluetoothAdapter == null) {
            if (D) { Log.e(TAG, "ERR: No Bluetooth Adapter"); }
            deviceArrayAdapter.add("Error. No Bluetooth Adapter present.");
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {
            if (D) { Log.d(TAG, "Enabling Bluetooth Adapter"); }
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            isEnabled = true;
            if (D) { Log.d(TAG, "Bluetooth Already Enabled"); }
            scanLeDevices(true);
        }
    }

    private void scanLeDevices(final boolean enable) {
        if (enable && !isScanning) {
            bluetoothAdapter.startLeScan(leDeviceCallback);
            isScanning = true;
        } else {
            bluetoothAdapter.stopLeScan(leDeviceCallback);
            isScanning = false;
        }
    }

    private BluetoothAdapter.LeScanCallback leDeviceCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            String formattedName = checkBeacon(device.getName());
            if (!formattedName.equals("Unknown")) {
                deviceHash.put(device.getAddress(), formattedName + "\n" + "RSSI: " + rssi + " dBm");
                rebuildArray();
            }
        }
    };

    private void rebuildArray() {
        deviceArrayAdapter.clear();
        for (Map.Entry<String, String> entry : deviceHash.entrySet()) {
            deviceArrayAdapter.add(entry.getValue());
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
            if (D) { Log.d(TAG, "Bluetooth Enabled"); }
            scanLeDevices(true);
        } else {
            if (D) { Log.e(TAG, "Failed turning on Bluetooth"); }
            finish();
        }
    }

    private String checkBeacon(String deviceLookup) {
        try {
            for (int i=0; i < animalData.length(); i++) {
                JSONObject item = animalData.getJSONObject(i);
                if (item.get("name").equals(deviceLookup)) {
                    return item.get("title").toString();
                }
            }
            return "Unknown";

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public JSONArray loadJSONFromAsset() {
        String json = null;
        JSONObject parsedData = null;
        try {
            InputStream is = HomeActivity.this.getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            parsedData = new JSONObject(json);
            return parsedData.getJSONArray("animals");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(D) { Log.e(TAG, "--- ON DESTROY ---"); }
    }
}
