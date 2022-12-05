package com.example.social_distancing_assistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class Advertiser {

    private BluetoothLeAdvertiser myAdvertiser;
    private String debug = "adv";
    private int myDiscoveredDevicesNum;

    public Advertiser() {
        this.myAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
    }

    @SuppressLint("MissingPermission")
    public String advertiseDevice(String mAndroidID, int numberOfDiscoveredDevices) {
        this.myDiscoveredDevicesNum = numberOfDiscoveredDevices;

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .setConnectable(false)
                .build();

        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(addAndroidID(mAndroidID)));

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(pUuid)
                .build();


        myAdvertiser.startAdvertising(settings, data, myAdvertisingCallback);
        return debug;
    }

    private AdvertiseCallback myAdvertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            debug = "onStartFailure";
            Log.e("BLE", "Advertising onStartFailure: " + errorCode);
            super.onStartFailure(errorCode);
        }
    };

    @SuppressLint("MissingPermission")
    public void stopAdvertising() {
        myAdvertiser.stopAdvertising(myAdvertisingCallback);
        return;
    }

    private String addAndroidID(String myAndroidID) {
        return myAndroidID.substring(0,8) + "-" + myAndroidID.substring(8, 12) + "-10"
                + myAndroidID.substring(12,14) + "-80" + myAndroidID.substring(14,16)
                + "-2800" + addNumberOfDiscoveredDevices() + "0042";
    }

    private String addNumberOfDiscoveredDevices() {
        String myNum = "0000";

        if(myDiscoveredDevicesNum < 10) {
            myNum = "000" + myDiscoveredDevicesNum;
        }
        else if(myDiscoveredDevicesNum < 100) {
            myNum = "00" + myDiscoveredDevicesNum;
        }
        else if(myDiscoveredDevicesNum < 1000) {
            myNum = "0" + myDiscoveredDevicesNum;
        }
        else if(myDiscoveredDevicesNum < 10000) {
            myNum = "" + myDiscoveredDevicesNum;
        }
        else {
            myNum = "9999";
        }

        return myNum;
    }
}
