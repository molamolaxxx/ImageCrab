package com.mola.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mola.interfaces.OnDownLoadFinishListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2018/7/23.
 */

public class ImageDownloadUtils {
    public  Boolean downLoadPic(String url,String path,OnDownLoadFinishListener inf){
        saveImage(getImageInputStream(url,inf),path);
        return true;
    }
    //通过url下载图片
    public  Bitmap getImageInputStream(String imageurl, OnDownLoadFinishListener inf){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(2000); //链接超时设置
            connection.setReadTimeout(2000);//设置读取超时
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            System.out.println("4########");
            InputStream inputStream=connection.getInputStream();
            System.out.println("5########");
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof TimeoutException){
                //超时异常
                inf.downloadTimeout();
            }
        }
        return bitmap;
    }
    public  void saveImage(Bitmap bitmap, String path){
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        System.out.println("6########");
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
            System.out.println("文件夹被创建！");
        }
        try {
            fileOutputStream=new FileOutputStream(path+"/"+System.currentTimeMillis()+".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
