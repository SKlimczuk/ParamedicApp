package com.example.paramedicapp.logic.impl;

import com.example.paramedicapp.logic.ParamedicLogic;

import java.util.Random;

public class DefaultParamedicLogic implements ParamedicLogic {
    @Override
    public int generateId() {
        Random random = new Random();
        return random.nextInt() % 10000 + 10000;
    }
}
