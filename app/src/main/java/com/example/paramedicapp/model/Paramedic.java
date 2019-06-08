package com.example.paramedicapp.model;

public class Paramedic {
    private int id;
    private double longitude;
    private double latitude;

    public Paramedic(int id) {
        this.id = id;
    }

    public Paramedic(int id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
