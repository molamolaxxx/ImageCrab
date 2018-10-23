package com.mola.objects;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/22.
 */

public class ImageLib {
    //图片的数量
    private int num;
    //图库的名称
    private String name;
    //图片集路径
    private ArrayList<String> imgPath;
    //图片库地址
    private String libPath;
    private Bitmap firstPic;
    public ImageLib(String libPath){
        this.libPath=libPath;
    }
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImgPath() {
        return imgPath;
    }

    public void setImgPath(ArrayList<String> imgPath) {
        this.imgPath = imgPath;
    }

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public Bitmap getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(Bitmap firstPic) {
        this.firstPic = firstPic;
    }
}
