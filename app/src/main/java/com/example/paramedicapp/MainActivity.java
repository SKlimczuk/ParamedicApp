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

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ParamedicLogic paramedicLogic = new DefaultParamedicLogic();

    public BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();

    private LocationManager locationManager;

    private Paramedic paramedic;

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

        broadcastButton = findViewById(R.id.broadcast);
        discoveryButton = findViewById(R.id.discovery);
        discoveryResult = findViewById(R.id.discoverySpace);
        paramedicIdView = findViewById(R.id.paramedicId);
        locationIdView = findViewById(R.id.gpsData);

        checkIfBleIsSupported();
        checkIfBtIsEnabled();
        checkIfGpsIsEnabled();
        checkLocationPermission();

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

//    public void advertise(View v) {
//        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
//
//        AdvertiseSettings settings = new AdvertiseSettings.Builder()
//                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
//                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
//                .setConnectable(true)
//                .build();
//
//        final String advData = "test";
//
//        AdvertiseData advertiseData = new AdvertiseData.Builder()
//                .setIncludeDeviceName(true)
//                .setIncludeTxPowerLevel(false)
//                .addManufacturerData(1, advData.getBytes(Charset.forName("UTF-8")))
//                .build();
//
//        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
//            @Override
//            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                Log.i("BLE", "started advertising with data:" + advData);
//                super.onStartSuccess(settingsInEffect);
//            }
//
//            @Override
//            public void onStartFailure(int errorCode) {
//                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
//                super.onStartFailure(errorCode);
//            }
//        };
//
//        advertiser.startAdvertising(settings, advertiseData, advertisingCallback);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        advertiser.stopAdvertising(advertisingCallback);
//    }

    public void discovery(View v) {
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
                //todo: metoda do przetwarzania danych
                builder.append("\n").append(
                        new String(result.getScanRecord().getManufacturerSpecificData(1))
                );

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
}
