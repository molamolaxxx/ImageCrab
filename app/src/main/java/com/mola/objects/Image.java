package com.mola.objects;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Image {
    private int height;
    private int width;
    private long size;
    private String path;
    private Bitmap bitmap;
    public Image(String path){
        this.path=path;
    }

    public float getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap,int height,int width) {
        this.bitmap = bitmap;
        this.height=height;
        this.width=width;
    }
}
