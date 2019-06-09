package com.example.paramedicapp.logic.impl;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.paramedicapp.logic.ParamedicLogic;
import com.example.paramedicapp.model.Paramedic;
import com.example.paramedicapp.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultParamedicLogic implements ParamedicLogic {

    public List<Patient> patients = new ArrayList<>();

    @Override
    public int generateId() {
        Random random = new Random();
        return random.nextInt() % 10000 + 11000;
    }

    public Patient encodeSensorAdvertisePacket(byte[] advertisedData) {
        String advDataConvertedToString = new String(advertisedData);
        String[] advDataArr = advDataConvertedToString.split(",");

        return new Patient(
                Integer.parseInt(advDataArr[0]),
                Integer.parseInt(advDataArr[1]),
                Integer.parseInt(advDataArr[2]),
                Integer.parseInt(advDataArr[3])
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String printEncodedSensorAdvertisePacket(byte[] advertisedData, Patient patient) {
        patient = encodeSensorAdvertisePacket(advertisedData);

        return  "id:" + patient.getId() + "," +
                "pulse:" + patient.getPulse() + "," +
                "bs:" + patient.getBloodSaturation() + "," +
                "bpm:" + patient.getBreathPerMinute();
    }

    @Override
    public byte[] customAdvertisingPacketGeneratorPatient(Patient patient) {
        String toConvert  =
                        patient.getId() + "," +
                        patient.getPulse() + "," +
                        patient.getBloodSaturation() + "," +
                        patient.getBreathPerMinute();

        return toConvert.getBytes();
    }

    @Override
    public byte[] customAdvertisingPacketGeneratorLocation(Paramedic paramedic) {
        String toConvert  =
                        paramedic.getId() + "," +
                        paramedic.getLongitude() + "," +
                        paramedic.getLatitude() + ",";

        return toConvert.getBytes();
    }
}
