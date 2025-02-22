package com.example.safetynet;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    private static final String EMERGENCY_ALERTS_PATH = "emergency_alerts";

    public static void sendEmergencyAlert(EmergencyAlert alert) {
        FirebaseDatabase.getInstance()
                .getReference(EMERGENCY_ALERTS_PATH)
                .push()
                .setValue(alert);
    }
}