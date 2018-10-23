package com.mola.objects;

/**
 * Created by Administrator on 2018/9/20.
 */

public class Setting {
    //设定爬虫数目
    private int picNum;
    private static int NORMAL_MOD;
    private int pxX;
    private int pxY;

    public int getPicNum() {
        return picNum;
    }

    public void setPicNum(int picNum) {
        this.picNum = picNum;
    }

    public static int getNormalMod() {
        return NORMAL_MOD;
    }

    public static void setNormalMod(int normalMod) {
        NORMAL_MOD = normalMod;
    }

    public int getPxX() {
        return pxX;
    }

    public void setPxX(int pxX) {
        this.pxX = pxX;
    }

    public int getPxY() {
        return pxY;
    }

    public void setPxY(int pxY) {
        this.pxY = pxY;
    }
}
