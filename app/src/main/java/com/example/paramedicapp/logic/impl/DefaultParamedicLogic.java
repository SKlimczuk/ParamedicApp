package com.example.paramedicapp.logic.impl;

import com.example.paramedicapp.logic.ParamedicLogic;
import com.example.paramedicapp.model.Patient;

import java.util.Random;

public class DefaultParamedicLogic implements ParamedicLogic {
    @Override
    public int generateId() {
        Random random = new Random();
        return random.nextInt() % 10000 + 11000;
    }

    @Override
    public void encodeSensorAdvertisePacket(byte[] advertisedData, Patient patient) {
        String advDataConvertedToString = new String(advertisedData);
        String[] advDataArr = advDataConvertedToString.split(",");

        //id
        patient.setId(Integer.parseInt(advDataArr[0]));
        //pulse
        patient.setId(Integer.parseInt(advDataArr[1]));
        //bs
        patient.setId(Integer.parseInt(advDataArr[2]));
        //bpm
        patient.setId(Integer.parseInt(advDataArr[3]));
    }

    @Override
    public String printEncodedSensorAdvertisePacket(byte[] advertisedData, Patient patient) {
        encodeSensorAdvertisePacket(advertisedData, patient);

        return  "id:" + patient.getId() + "," +
                "pulse:" + patient.getPulse() + "," +
                "bs:" + patient.getBloodSaturation() + "," +
                "bpm:" + patient.getBreathPerMinute();
    }
}
