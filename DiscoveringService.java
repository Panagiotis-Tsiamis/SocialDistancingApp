package com.example.social_distancing_assistant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DiscoveringService extends JobIntentService implements LocationListener {

    static final int JOB_ID = 1000;

    private Notifier mNotifier;
    private static Context mContext;
    private Double mLatitude;
    private Double mLongitude;
    private String mCity;
    private Handler mHandler = new Handler();
    private int mDiscoveredDevicesNum = 0;
    private LocationManager locationManager;

    static void enqueueWork(Context context, Intent work) {
        mContext = context;
        enqueueWork(context, DiscoveringService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if(intent.getAction() == "LOAD_WHITELIST") {
            mNotifier = new Notifier(this);
            Intent mIntent = new Intent("whitelist_transfer");
            mIntent.putStringArrayListExtra("whitelist", mNotifier.getWhitelist());
            LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
        }
        else if(intent.getAction() == "CROWD_DETECTION") {

            mDiscoveredDevicesNum = intent.getIntExtra("mDiscoveredDevicesNum" , -1);

            updateDatabase();
        }
    }

    private void updateDatabase() {
        updateLocation();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                writeToDatabase();
            }
        }, 500);
    }

    private void updateLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Location mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(mLocation == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, this, Looper.getMainLooper());
                onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            onLocationChanged(mLocation);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            Log.e("here", location.toString());
            locationManager.removeUpdates(this);
        }
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

    private void writeToDatabase() {
        String mAndroidID = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //locateCity();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mItem = mDatabase.getReference(mAndroidID);

        mItem.child("DiscoveredDevicesNum").setValue("" + mDiscoveredDevicesNum);
//        mItem.child("Location").child("City").setValue("" + mCity);
        mItem.child("Location").child("Latitude").setValue("" + mLatitude);
        mItem.child("Location").child("Longitude").setValue("" + mLongitude);
    }

//    private void locateCity() {
//        try {
//            Geocoder geocoder = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
//            Log.e("here", ""  + addresses);
//            if (addresses != null && addresses.size() > 0) {
//                mCity = addresses.get(0).getLocality();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("catch", "error");
//        }
//    }
}