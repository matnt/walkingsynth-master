package com.dobi.walkingsynth.accelerometer;

import java.io.Serializable;

/**
 * Created by Sujin on 12/2/2017.
 */
public class Accelerometer implements Serializable{
    private double tb, valueafterfilt;
    private int step_number;

    public double getTb() {
        return tb;
    }

    public Accelerometer(double tb, double valueafterfilt, int step_number) {
        this.tb = tb;
        this.step_number = step_number;
        this.valueafterfilt = valueafterfilt;
    }

    public Accelerometer(double tb, double valueafterfilt) {
        this.tb = tb;
        this.valueafterfilt = valueafterfilt;
    }

    public void setTb(float tb) {
        this.tb = tb;
    }

    public double getValueafterfilt() {
        return valueafterfilt;
    }

    public void setValueafterfilt(float valueafterfilt) {
        this.valueafterfilt = valueafterfilt;
    }

    public int getStep_number() {
        return step_number;
    }

    public void setStep_number(int step_number) {
        this.step_number = step_number;
    }
}
