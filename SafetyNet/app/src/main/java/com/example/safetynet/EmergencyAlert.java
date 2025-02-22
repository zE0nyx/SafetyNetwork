package com.example.safetynet;

public class EmergencyAlert {
    private double latitude;
    private double longitude;
    private String deviceToken;
    private long timestamp;

    public EmergencyAlert(double latitude, double longitude, String deviceToken) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceToken = deviceToken;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
}