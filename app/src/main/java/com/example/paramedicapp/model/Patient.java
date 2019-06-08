package com.example.paramedicapp.model;

public class Patient {
    private int id;
    private int pulse;
    private int bloodSaturation;
    private int breathPerMinute;

    public Patient() {
    }

    public Patient(int id,
                   int pulse,
                   int bloodSaturation,
                   int breathPerMinute) {
        this.id = id;
        this.pulse = pulse;
        this.bloodSaturation = bloodSaturation;
        this.breathPerMinute = breathPerMinute;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getBloodSaturation() {
        return bloodSaturation;
    }

    public void setBloodSaturation(int bloodSaturation) {
        this.bloodSaturation = bloodSaturation;
    }

    public int getBreathPerMinute() {
        return breathPerMinute;
    }

    public void setBreathPerMinute(int breathPerMinute) {
        this.breathPerMinute = breathPerMinute;
    }
}
