package com.beepmate.io.notificationlistenertester;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class NotificationFragment extends PreferenceFragmentCompat {
    SharedPreferences sPreferences;
    private Context context;
    private final CharSequence channelName = "Default Channel";
    private final String channelDescription = "Default Channel";
    final String CHANNEL_ID = "Default notification";

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.notification_fragment, rootKey);
        context = getActivity();
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        PreferenceScreen send_notification = (PreferenceScreen) findPreference("send_notification");
        send_notification.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        Log.i("NotificationListenderTester", "Sending notification");
                        sendNotification(context);
                        return true;
                    }
                });
    }

    public void sendNotification(Context context) {
        Log.i("NotificationListenderTester", "sendNotification");

        int notificationId = 0;
        if (!sPreferences.getBoolean("pref_reuse_notification_id", true)) {
            notificationId = (int) ((System.currentTimeMillis() / 1000L) % Integer.MAX_VALUE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, Integer.parseInt(sPreferences.getString("pref_importance", "3"))); //NotificationManager.IMPORTANCE_HIGH
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        String contentTitle = sPreferences.getString("pref_title", "Notification Tester Title");
        String contentText = sPreferences.getString("pref_text", "Notification Tester Text");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(Integer.parseInt(sPreferences.getString("pref_priority", "0"))) // NotificationCompat.PRIORITY_DEFAULT
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentText(contentText);


        builder.setOngoing(sPreferences.getBoolean("pref_ongoing", false));
        builder.setGroupSummary(sPreferences.getBoolean("pref_group_summary", false));
        builder.setSilent(sPreferences.getBoolean("pref_silent", false));

        if (!sPreferences.getString("pref_group", "").equals("")) {
            builder.setGroup(sPreferences.getString("pref_group", "Group"));
        }

        if (sPreferences.getBoolean("pref_add_dismiss", true)) {
            Intent intentDismiss = new Intent(context, BroadcastReceiver.class);
            intentDismiss.putExtra("notificationId", notificationId);
            intentDismiss.setAction("DISMISS_ACTION");

            PendingIntent pendingIntentDismiss = PendingIntent.getBroadcast(context, notificationId, intentDismiss, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(0, "DISMISS", pendingIntentDismiss);
        }

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, notification);
    }
}
