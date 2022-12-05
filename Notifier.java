package com.example.social_distancing_assistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Notifier {

    private FileManager myFileManager;
    private Context myContext;
    private ArrayList<String> myWhiteList = new ArrayList<>();

    public Notifier(Context context) {
        this.myContext = context;
        this.myFileManager = new FileManager(myContext);
        loadWhitelist();
    }

    public Notifier(Context context, ArrayList<String> whiteList) {
        this.myContext = context;
        this.myFileManager = new FileManager(myContext);
        this.myWhiteList = whiteList;
    }

    public ArrayList<String> getWhitelist() {
        return this.myWhiteList;
    }

    public boolean distanceNotification(String myAndroidID, int mRssi) {

        if (Math.abs(mRssi) < 75 && !myWhiteList.contains(myAndroidID)) {
            NotificationChannel mChannel02;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel02 = new NotificationChannel("channel02", "Too close warning",
                        NotificationManager.IMPORTANCE_HIGH);

                mChannel02.setVibrationPattern(new long[]{100, 500, 250, 500, 250, 500});
                mChannel02.enableVibration(true);
                mChannel02.setLightColor(Color.RED);
                mChannel02.enableLights(true);

                NotificationManager notificationManager =
                        (NotificationManager) myContext.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel02);
            }

            Notification notification = new NotificationCompat.Builder(myContext, "channel02")
                    .setSmallIcon(R.drawable.ic_distance_too_close)
                    .setContentTitle("You Are Too Close")
                    .setContentText("Keep Your Distance")
                    .setColor(15017472)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            NotificationManagerCompat notificationManage = NotificationManagerCompat.from(myContext);
            notificationManage.notify(2842, notification);
            return true;
        }
        else if(Math.abs(mRssi) < 83 && !myWhiteList.contains(myAndroidID)) {
            NotificationChannel mChannel03;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel03 = new NotificationChannel("channel03", "Safe distance violation",
                        NotificationManager.IMPORTANCE_DEFAULT);

                mChannel03.setVibrationPattern(new long[]{100, 500, 250, 500});
                mChannel03.enableVibration(true);
                mChannel03.setLightColor(Color.YELLOW);
                mChannel03.enableLights(true);

                NotificationManager notificationManager =
                        (NotificationManager) myContext.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel03);
            }

            Notification notification = new NotificationCompat.Builder(myContext, "channel03")
                    .setSmallIcon(R.drawable.ic_distance_warning)
                    .setContentTitle("Safe Distance Violation")
                    .setContentText("Keep Your Distance")
                    .setColor(16745472)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            NotificationManagerCompat notificationManage = NotificationManagerCompat.from(myContext);
            notificationManage.notify(2842, notification);
            return true;
        }
        else {
            return false;
        }
    }

    public void crowdDetectedNotification() {
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channel04", "Crowd detection",
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager =
                    (NotificationManager) myContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(myContext, "channel04")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Crowd Detected")
                .setContentText("Keep Your Distance")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManagerCompat notificationManage = NotificationManagerCompat.from(myContext);
        notificationManage.notify(2843, notification);
    }

    private void loadWhitelist() {
        myFileManager.checkFileExistence();

        try {
            myWhiteList = myFileManager.loadWhiteListFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
