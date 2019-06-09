package com.example.paramedicapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paramedicapp.bluetooth.BluetoothConstants;
import com.example.paramedicapp.logic.ParamedicLogic;
import com.example.paramedicapp.logic.impl.DefaultParamedicLogic;
import com.example.paramedicapp.model.Paramedic;
import com.example.paramedicapp.model.Patient;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ParamedicLogic paramedicLogic = new DefaultParamedicLogic();

    public BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeAdvertiser advertiser;
    private Handler mHandler = new Handler();

    private LocationManager locationManager;

    private Paramedic paramedic;
    private Patient patient;

    private Button broadcastButton;
    private Button discoveryButton;
    private TextView discoveryResult;
    private TextView paramedicIdView;
    private TextView locationIdView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paramedic = new Paramedic(paramedicLogic.generateId(), 0, 0);
        patient = new Patient();

        broadcastButton = findViewById(R.id.broadcast);
        discoveryButton = findViewById(R.id.discovery);
        discoveryResult = findViewById(R.id.discoverySpace);
        paramedicIdView = findViewById(R.id.paramedicId);
        locationIdView = findViewById(R.id.gpsData);

        checkIfBleIsSupported();
        checkIfBtIsEnabled();
        checkIfGpsIsEnabled();
        checkLocationPermission();

        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        onLocationChanged(location);

        printParamedicId(paramedic);
    }

    private void checkIfBleIsSupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble not supported", Toast.LENGTH_LONG).show();
        }
    }

    private void checkIfBtIsEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothConstants.REQUEST_ENABLE_BT);
        }
    }

    public void advertise(View v) {
        stopDiscovery();

        signalingOfBroadcastStateChanged(true);

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(false)
                .build();

        byte[] advData = paramedicLogic.customAdvertisingPacketGeneratorPatient(patient);

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(1, advData)
                .build();

        byte[] advData2 = paramedicLogic.customAdvertisingPacketGeneratorLocation(paramedic);

        AdvertiseData advertiseData2 = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(1, advData)
                .build();

        AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i("BLE", "started advertising");
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising(settings, advertiseData, advertiseCallback);
        advertiser.startAdvertising(settings, advertiseData2, advertiseCallback);
    }

    private void stopAdvertise() {
        AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        };

        advertiser.stopAdvertising(advertiseCallback);
        signalingOfBroadcastStateChanged(false);
    }

    public void discovery(View v) {
        stopAdvertise();

        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        List<ScanFilter> filters = new LinkedList<>();

        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceName("s")
                .build();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();

        ScanCallback mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                StringBuilder builder = new StringBuilder("VICTIM DETECTED");

                try {
                    builder.append("\n").append(
                            paramedicLogic.printEncodedSensorAdvertisePacket(
                                    result.getScanRecord().getManufacturerSpecificData(1), patient
                            )
                    );
                    patient = paramedicLogic.encodeSensorAdvertisePacket(result.getScanRecord().getManufacturerSpecificData(1));
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                discoveryResult.setText(builder);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e("BLE", "Discovery onScanFailed: " + errorCode);
                super.onScanFailed(errorCode);
            }
        };

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }

    public void stopDiscovery() {
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        mBluetoothLeScanner.stopScan(scanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkIfGpsIsEnabled() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        paramedic.setLatitude(location.getLatitude());
        paramedic.setLongitude(location.getLongitude());

        locationIdView.setText("" + paramedic.getLatitude() + "\n" + paramedic.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void printParamedicId(Paramedic paramedic) {
        paramedicIdView.setText("" + paramedic.getId());
    }

    private void signalingOfBroadcastStateChanged(boolean state) {
        if (state)
            broadcastButton.getBackground()
                    .setColorFilter(new LightingColorFilter(Color.TRANSPARENT, Color.BLUE));
        else
            broadcastButton.getBackground()
                    .setColorFilter(new LightingColorFilter(Color.TRANSPARENT, Color.BLACK));
    }
}
