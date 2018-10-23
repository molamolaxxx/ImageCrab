package com.mola.utils;

/**
 * Created by Administrator on 2018/7/21.
 * 文件操作类
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mola.control.DownBaiduPicture;
import com.mola.objects.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class FileCompratorUtils {
    //连文件夹全部删除
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //删除文件夹所有文件
    public static boolean delAllFile(String path)
    {
        //标志，判断文件有没有删光，false表示文件不存在或不是文件夹
        boolean flag = false;
        File file = new File(path);
        //文件夹不存在
        if (!file.exists()) {
            return flag;
        }
        //不是文件夹
        if (!file.isDirectory()) {
            return flag;
        }
        //获取文件路径列表
        String[] tempList = file.list();
        if(tempList==null)
            return false;
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
    //给定一个文件夹，将里面的图片文件全部转化成数组
    public  ArrayList<Image> parseImageFileToObject(String folderPath){
        File file=new File(folderPath);
        ArrayList<Image> imageList=new ArrayList<>();
        if(!file.exists()){
            System.out.println("文件不存在！");
            return null;
        }
        if(!file.isDirectory()){
            System.out.println("不是文件夹！");
            return null;
        }
        String[] fileList=file.list();
        for (int i=0;i<fileList.length;i++){
            Image im;
            //如果是损坏文件,删除并直接return
            String filePath=null;
            if (folderPath.endsWith(File.separator)) {
                filePath=folderPath + fileList[i];
            } else {
                filePath=folderPath + File.separator + fileList[i];
            }
            //如果文件损坏,直接跳过
            File f=new File(filePath);
            if(f.length()==0) {
                //失败，多爬一张

                continue;
            }
            im=new Image(filePath);
            im.setSize(f.length());
            getImageImformation(im);
            imageList.add(im);
        }
        return imageList;
    }
    public Image getLatestImage(String folderPath){
        File file=new File(folderPath);
        if(!file.exists()){
            System.out.println("文件不存在！");
            return null;
        }
        if(!file.isDirectory()){
            System.out.println("不是文件夹！");
            return null;
        }
        String[] fileList=file.list();
        int length=fileList.length-1;
        String filePath=null;
        if (folderPath.endsWith(File.separator)) {
            filePath=folderPath + fileList[length];
        } else {
            filePath=folderPath + File.separator + fileList[length];
        }
        File f=new File(filePath);
        if(f.length()==0){
            DownBaiduPicture.addOnePic();
            return new Image("1");
        }
        Image im=new Image(filePath);
        im.setSize(f.length());
        getImageImformation(im);
        return im;
    }
    public  void getImageImformation(Image im)
    {
        BitmapFactory.Options options=new BitmapFactory.Options();
        Bitmap bp=BitmapFactory.decodeFile(im.getPath());
        //再此进行图片压缩
        //长宽分别变成原来的四分之一，大小变成十六分之一
        if(bp.getByteCount()>10000000){
            options.inSampleSize=8;
        }
        else if(bp.getByteCount()>5000000){
            options.inSampleSize=6;
        }
        else if(bp.getByteCount()>1000000){
            options.inSampleSize=4;
        }
        else
            options.inSampleSize=2;
        Bitmap bitmap= BitmapFactory.decodeFile(im.getPath(),options);
        System.out.println("原来的大小为"+bp.getByteCount()+" 压缩后的大小是"+bitmap.getByteCount()+
                " 压缩值为"+options.inSampleSize+" 大小为"+im.getSize()/1000 + "kb");
        im.setBitmap(bitmap,bp.getHeight(),bp.getWidth());
        bp.recycle();
        System.gc();
    }
    public Boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            return false;
        }
    }
}





