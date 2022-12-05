package com.example.social_distancing_assistant;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 98;

    private TextView mAndroidIDText;
    private Switch mVisibilitySwitch;
    private ListView mScanList;
    private Button mScanningButton;
    private ProgressBar mScanningProgressBar;
    private Discoverer mDiscoverer;
    private Advertiser mAdvertiser;
    private String mAndroidID;
    private ArrayList<String> mWhiteListArray;
    private FileManager mFileManager;
    private Handler mHandler = new Handler();
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager mLocationManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVisibilitySwitch = (Switch) findViewById(R.id.visibility_switch);
        mAndroidIDText = (TextView) findViewById(R.id.androidIDText);
        mScanList = (ListView) findViewById(R.id.scan_list);

        mVisibilitySwitch.setText("  Phone visibility \n    " +
                "Make your phone visible to nearby devices.");

        mScanningButton = (Button) findViewById(R.id.scanning_btn);
        mScanningButton.setOnClickListener(this);

        mScanningProgressBar = (ProgressBar) findViewById(R.id.scanning_progress_bar);

        // 16 bit
        mAndroidID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mAndroidIDText.setText("Android ID : " + mAndroidID + "\n\nNEARBY DEVICES :");

        mFileManager = new FileManager(this);
        mFileManager.checkFileExistence();

        try {
            mWhiteListArray = mFileManager.loadWhiteListFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Intent mTutorialIntent = new Intent(MainActivity.this,
                    TutorialPage1Activity.class);
            MainActivity.this.startActivity(mTutorialIntent);
        }

        while (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = this;
        if (!mBluetoothAdapter.isEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage("To continue you need to turn on the bluetooth")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
//                                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                                        Manifest.permission.BLUETOOTH_CONNECT)) {
//                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                        ActivityCompat.requestPermissions(MainActivity.this,
//                                                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
//                                                MY_PERMISSIONS_REQUEST_BLUETOOTH);
//                                    }
//                                }
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                                }
                                mBluetoothAdapter.enable();
                                Toast.makeText(mContext, "Bluetooth has been enabled", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setMessage("To continue you need to turn on device location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            } catch (Exception e) {
                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

        mVisibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAdvertiser = new Advertiser();
                    mAdvertiser.advertiseDevice(mAndroidID, 0);
                } else {
                    mAdvertiser.stopAdvertising();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanning_btn) {
            mScanningProgressBar.setVisibility(View.VISIBLE);
            mDiscoverer = new Discoverer(this);
            mDiscoverer.clearArrays();
            mDiscoverer.discoverNearbyDevices();
            mContext = this;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanList.setAdapter(new MyScanListAdapter(mDiscoverer.getMAndroidIDArray(),
                            mDiscoverer.getMRssisArray(), mWhiteListArray, mContext));
                    mScanningProgressBar.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_whitelist:
                Intent mWhitelistIntent = new Intent(MainActivity.this,
                        WhitelistActivity.class);
                MainActivity.this.startActivity(mWhitelistIntent);
                return true;

            case R.id.action_help:
                Intent mTutorialIntent = new Intent(MainActivity.this,
                        TutorialPage1Activity.class);
                MainActivity.this.startActivity(mTutorialIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdvertiser != null)
            mAdvertiser.stopAdvertising();
        mFileManager.saveWhiteListToFile(mWhiteListArray);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}