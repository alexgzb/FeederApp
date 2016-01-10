package com.gezelbom.feederapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by Alex
 *
 * Intent that is called by the alarmManager and triggers the Notification received in the intent
 */
public class NotificationPublish extends BroadcastReceiver{

    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get NotificationManager from System
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Cancel Previous
        nManager.cancel(0);
        //Fetch the Notification from the intent
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        //Notify and vibrate for 1 sec
        nManager.notify(id, notification);
        ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
    }
}
