package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.xamoom.android.XamoomBeaconService;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

/**
 * Created by raphaelseher on 25.11.15.
 */
public class XamoomPingeborgApp extends Application {
    private static final String TAG = XamoomPingeborgApp.class.getSimpleName();
    private static final String MAJOR_ID = "52414";
    private static final int BEACON_NOTIFICATION_ID = 1;
    public static final String BEACON_NOTIFICATION = "BEACON_NOTIFICATION";


    @Override
    public void onCreate() {
        super.onCreate();
        XamoomBeaconService.getInstance(getApplicationContext()).startBeaconService(MAJOR_ID);

        registerReceiver(mEnterRegionBroadCastReciever, new IntentFilter(XamoomBeaconService.ENTER_REGION_BROADCAST));
        registerReceiver(mExitRegionBroadCastReciever, new IntentFilter(XamoomBeaconService.EXIT_REGION_BROADCAST));
    }

    private final BroadcastReceiver mEnterRegionBroadCastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "background: enterRegion");

            Intent activityIntent = new Intent(XamoomPingeborgApp.this, MainActivity.class);
            activityIntent.putExtra(BEACON_NOTIFICATION, true);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
            notificationBuilder
                    .setContentTitle("Discover a local artist")
                    .setContentText("Open pingeb.org app")
                    .setContentInfo("Near you")
                    .setSmallIcon(R.drawable.ic_xamoom_ble)
                    .setContentIntent(pendingIntent);

            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(BEACON_NOTIFICATION_ID, notification);
        }
    };

    private final BroadcastReceiver mExitRegionBroadCastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "background: exitRegion");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.cancel(BEACON_NOTIFICATION_ID);
        }
    };
}
