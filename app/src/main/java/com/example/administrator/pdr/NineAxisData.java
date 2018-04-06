package com.example.administrator.pdr;

/**
 * Created by yuekang on 2018/3/23.
 */

public class NineAxisData {

    private static float[] accData;
    private static float[] gyroData;
    private static float[] magData;

    public NineAxisData() {
        //
    }


    public void setAccData(float[] accData) {
        this.accData = accData;
    }
    public void setGyroData(float[] gyroData) {
        this.gyroData = gyroData;
    }
    public void setMagData(float[] magData) {
        this.magData = magData;
    }

    public static float[] getAccData() {
        return accData;
    }
    public static float[] getGyroData() {
        return gyroData;
    }
    public static float[] getMagData() {
        return magData;
    }
}
