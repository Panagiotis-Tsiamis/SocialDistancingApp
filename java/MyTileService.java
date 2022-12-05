package com.example.social_distancing_assistant;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileService extends TileService {

    private Intent myIntent;
    private int mState;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        this.getQsTile().setState(Tile.STATE_INACTIVE);
        mState = Tile.STATE_INACTIVE;
        this.getQsTile().updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!mBluetoothAdapter.isEnabled() ||
                !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            this.getQsTile().setState(Tile.STATE_UNAVAILABLE);
        }
        else {
            this.getQsTile().setState(mState);
        }
        this.getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Intent mMainIntent = new Intent(this, MainActivity.class);
            mMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mMainIntent);
            this.getQsTile().setState(Tile.STATE_INACTIVE);
            mState = Tile.STATE_INACTIVE;
            this.getQsTile().updateTile();
            return;
        }

        myIntent = new Intent(this, ADService.class);
        if(this.getQsTile().getState() == Tile.STATE_INACTIVE) {
            //Turn ON
            this.getQsTile().setState(Tile.STATE_ACTIVE);
            mState = Tile.STATE_ACTIVE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(myIntent);
            }
            else {
                startService(myIntent);
            }
        }
        else {
            //Turn OFF
            this.getQsTile().setState(Tile.STATE_INACTIVE);
            mState = Tile.STATE_INACTIVE;
            stopService(myIntent);
        }
        this.getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        this.getQsTile().updateTile();
    }
}