package com.example.paramedicapp.logic;

import com.example.paramedicapp.model.Paramedic;
import com.example.paramedicapp.model.Patient;

public interface ParamedicLogic {
    /**
     * @return auto generated id
     */
    int generateId();

    /**
     * method will encode ble advertisment
     *
     * @param advertisedData data discovered during ble scannign
     */
    Patient encodeSensorAdvertisePacket(byte[] advertisedData);

    /**
     * @param advertisedData data discovered during ble scannign
     * @return nice formatted string with encoded data
     */
    String printEncodedSensorAdvertisePacket(byte[] advertisedData, Patient patient);

    /**
     * @param patient to retrieve data
     * @return data as string
     */
    byte[] customAdvertisingPacketGeneratorPatient(Patient patient);
    byte[] customAdvertisingPacketGeneratorLocation(Paramedic paramedic);
}
