package com.beepmate.io.notificationlistenertester;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationListenderTester", "BroadcastReceiver onReceive: " +  intent.getAction() + " notificationId " + intent.getIntExtra("notificationId", 0) );
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("notificationId", 0));
    }
}
