package com.example.social_distancing_assistant;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ADService extends Service {

    private ArrayList<String> mWhiteList = new ArrayList<>();

    private Discoverer mDiscoverer;
    private Advertiser mAdvertiser = new Advertiser();
    private String mAndroidID;
    private Handler mHandler = new Handler();
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("whitelist_transfer"));
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent mServiceIntent = new Intent();
        mServiceIntent.setAction("LOAD_WHITELIST");
        DiscoveringService.enqueueWork(this, mServiceIntent);

        mAndroidID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDiscoverer = new Discoverer(mContext, mWhiteList);

                ExecutorService myExecutor = Executors.newFixedThreadPool(2);
                myExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mAdvertiser.advertiseDevice(mAndroidID, 0);
                    }
                });
                myExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDiscoverer.discoverDevices();
                    }
                });
                myExecutor.shutdown();
            }
        }, 1000);

        startForeground();

        super.onStartCommand(intent, flags, startId);

        return Service.START_STICKY;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWhiteList = (ArrayList<String>) intent.getStringArrayListExtra("whitelist");
        }
    };

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channel01", "Run background service",
                    NotificationManager.IMPORTANCE_MIN);

            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "channel01")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_distance_tile)
                .setContentTitle("Social distancing assistant is running")
                .setColor(88028)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdvertiser.stopAdvertising();
        mDiscoverer.stopDiscovering();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
