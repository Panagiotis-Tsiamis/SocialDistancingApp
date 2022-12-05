package com.example.social_distancing_assistant;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Discoverer {

    private BluetoothLeScanner myBluetoothLeScanner;
    private Handler myHandler = new Handler();

    private ArrayList<String> myAndroidIDArray = new ArrayList<>();
    private ArrayList<Integer> myRssisArray = new ArrayList<>();
    private ArrayList<Integer> myTimesArray = new ArrayList<>();
    private Notifier myNotifier;
    private Context myContext;
    private int mDiscoveredDevicesNum;

    public Discoverer(Context context) {
        this.myBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        this.myContext = context;
        this.myNotifier = new Notifier(myContext);
    }

    public Discoverer(Context context, ArrayList<String> whiteList) {
        this.myBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        this.myContext = context;
        this.myNotifier = new Notifier(myContext, whiteList);
    }

    public void discoverDevices() {
        ParcelUuid uuidMask = new ParcelUuid(UUID.fromString("00000000-0000-FF00-FF00-FFFF0000FFFF"));

        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID
                        .fromString("00000000-0000-1000-8000-280000000042")), uuidMask)
                .build();
        filters.add(filter);

        ScanSettings settings = null;
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

        }
        myBluetoothLeScanner.stopScan(myScanCallback);
        myBluetoothLeScanner.startScan(filters, settings, myScanCallback);
    }

    public void stopDiscovering() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

        }
        myBluetoothLeScanner.stopScan(myScanCallback);
        return;
    }

    private ScanCallback myScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null) {
                return;
            }

            String myUuid = result.getScanRecord().getServiceUuids().get(0).toString();

            mDiscoveredDevicesNum = myAndroidIDArray.size();

            resultHandler(extractAndroidID(myUuid), result.getRssi(),
                    extractDiscoveredDevicesNum(myUuid), 0);
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

    public void discoverNearbyDevices() {
        ParcelUuid uuidMask = new ParcelUuid(UUID.fromString("00000000-0000-FF00-FF00-FFFF0000FFFF"));

        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID
                        .fromString("00000000-0000-1000-8000-280000000042")), uuidMask)
                .build();
        filters.add(filter);

        ScanSettings settings = null;
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

        }
        myBluetoothLeScanner.stopScan(myNearScanCallback);
        myBluetoothLeScanner.startScan(filters, settings, myNearScanCallback);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                }
                myBluetoothLeScanner.stopScan(myNearScanCallback);
            }
        }, 2000);
    }

    private ScanCallback myNearScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result == null) {
                return;
            }

            String myUuid = result.getScanRecord().getServiceUuids().get(0).toString();

            resultHandler(extractAndroidID(myUuid), result.getRssi(),
                    extractDiscoveredDevicesNum(myUuid), 1);
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

    private String extractAndroidID(String myUuid) {
        return myUuid.substring(0,8) + myUuid.substring(9,13) + myUuid.substring(16,18)
                + myUuid.substring(21,23);
    }

    private int extractDiscoveredDevicesNum(String myUuid) {
        return Integer.parseInt(myUuid.substring(28,32));
    }

    private void resultHandler(String myAndroidID, int myRssi, int myDiscoveredDevicesNum, int flag) {
        checkTimes();

        if(!myAndroidIDArray.contains(myAndroidID)) {
            myAndroidIDArray.add(myAndroidID);
            myRssisArray.add(myRssi);
            myTimesArray.add(getTime());
        }
        else {
            if(myRssisArray.get(myAndroidIDArray.indexOf(myAndroidID)) > myRssi + 2 ||
                    myRssisArray.get(myAndroidIDArray.indexOf(myAndroidID)) < myRssi + 2) {
                myRssisArray.set(myAndroidIDArray.indexOf(myAndroidID), myRssi);
            }
        }

        if(flag == 0) {
            notifyUser();
        }

        if(myAndroidIDArray.size() > 0 && myAndroidIDArray.size() != mDiscoveredDevicesNum) {
            mDiscoveredDevicesNum = myAndroidIDArray.size();
            Intent mServiceIntent = new Intent();
            mServiceIntent.setAction("CROWD_DETECTION");
            mServiceIntent.putExtra("mDiscoveredDevicesNum", mDiscoveredDevicesNum);
            DiscoveringService.enqueueWork(myContext, mServiceIntent);
        }
    }

    private void notifyUser() {
        for(int i = 0; i < myAndroidIDArray.size(); i ++) {
            if (getTime() > myTimesArray.get(i) + 5) {
                if (myNotifier.distanceNotification(myAndroidIDArray.get(i), myRssisArray.get(i))) {
                    myTimesArray.set(i, getTime());
                }
            }
        }
    }

//    private double calculateDistance(int rssi) {
//        double res = (-80 - rssi);
//        if(Math.abs(rssi) < 80) {
//            res = res / 20;
//        }
//        else if(Math.abs(rssi) < 90) {
//            res = res / 20;
//        }
//        else {
//            res = res / 30;
//        }
//        res = Math.pow(10, res);
//        return res;
//    }

    private void checkTimes() {
        int currentTime = getTime();

        for(int i = 0; i < myTimesArray.size(); i ++) {
            if(currentTime > myTimesArray.get(i) + 30) {
                myAndroidIDArray.remove(i);
                myRssisArray.remove(i);
                myTimesArray.remove(i);
            }
        }
    }

    private int getTime() {
        int time = 0;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter mins = DateTimeFormatter.ofPattern("mm");
            DateTimeFormatter secs = DateTimeFormatter.ofPattern("ss");
            LocalDateTime now = LocalDateTime.now();
            int minutes = Integer.parseInt(mins.format(now));
            int seconds = Integer.parseInt(secs.format(now));
            time = minutes * 60 + seconds;
        }
        return time;
    }

    public ArrayList<String> getMAndroidIDArray() {
        if(myAndroidIDArray.size() > 0) {
            return this.myAndroidIDArray;
        }
        else {
            return new ArrayList<String>() {
                {
                    add("N/A");
                }
            };
        }
    }

    public ArrayList<Integer> getMRssisArray() {
        if(myRssisArray.size() > 0) {
            return this.myRssisArray;
        }
        else {
            return new ArrayList<Integer>() {
                {
                    add(-1);
                }
            };
        }
    }

    public void clearArrays() {
        myAndroidIDArray.clear();
        myRssisArray.clear();
        myTimesArray.clear();
    }
}
