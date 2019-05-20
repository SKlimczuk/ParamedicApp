package com.example.paramedicapp.model;

public class Patient {
    private int id;
    private int heartRate;
    private int bloodSaturation;
    private int breathePerMinute;
    private int latitude;
    private int longitude;

    public Patient() {
    }

    public Patient(int id,
                   int heartRate,
                   int bloodSaturation,
                   int breathePerMinute,
                   int latitude,
                   int longitude) {
        this.id = id;
        this.heartRate = heartRate;
        this.bloodSaturation = bloodSaturation;
        this.breathePerMinute = breathePerMinute;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBloodSaturation() {
        return bloodSaturation;
    }

    public void setBloodSaturation(int bloodSaturation) {
        this.bloodSaturation = bloodSaturation;
    }

    public int getBreathePerMinute() {
        return breathePerMinute;
    }

    public void setBreathePerMinute(int breathePerMinute) {
        this.breathePerMinute = breathePerMinute;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
