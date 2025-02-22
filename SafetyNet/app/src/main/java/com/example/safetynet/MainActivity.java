package com.example.safetynet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private long lastPowerButtonPress = 0;
    private int powerButtonPressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Firebase
        FirebaseMessaging.getInstance().subscribeToTopic("emergencyAlerts");

        // Emergency button setup
        Button emergencyButton = findViewById(R.id.emergencyButton);
        emergencyButton.setOnClickListener(v -> sendEmergencySignal());

        // Request necessary permissions
        requestPermissions();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastPowerButtonPress <= 1000) {
                powerButtonPressCount++;
                if (powerButtonPressCount >= 4) {
                    sendEmergencySignal();
                    powerButtonPressCount = 0;
                }
            } else {
                powerButtonPressCount = 1;
            }

            lastPowerButtonPress = currentTime;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendEmergencySignal() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            sendAlert(location);
                        }
                    });
        }
    }

    private void sendAlert(Location location) {
        EmergencyAlert alert = new EmergencyAlert(
                location.getLatitude(),
                location.getLongitude(),
                FirebaseMessaging.getInstance().getToken().getResult()
        );

        // Send to Firebase
        FirebaseUtils.sendEmergencyAlert(alert);

        // Send to police API
        PoliceApiClient.sendEmergencySignal(alert);

        // Start emergency service
        Intent serviceIntent = new Intent(this, EmergencyService.class);
        serviceIntent.putExtra("location", location);
        startService(serviceIntent);
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WAKE_LOCK
                },
                PERMISSION_REQUEST_CODE);
    }
}