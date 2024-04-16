package com.beepmate.io.notificationlistenertester;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionState = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
            // If the permission is not granted, request it.
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new NotificationFragment())
                .addToBackStack(null)
                .commit();
    }
}