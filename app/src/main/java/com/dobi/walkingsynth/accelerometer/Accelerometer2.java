package com.dobi.walkingsynth.accelerometer;

/**
 * Created by Sujin on 12/22/2017.
 */
public class Accelerometer2 {
    private double x, y, z;
    private int n;

    public Accelerometer2(double x, double y, double z, int n) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.n = n;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
