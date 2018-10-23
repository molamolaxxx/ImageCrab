package com.mola.objects;


import android.content.Context;
import android.os.Message;

import com.mola.control.DownBaiduPicture;
import com.mola.interfaces.OnDownLoadFinishListener;

import java.io.IOException;

/**
 * Created by molamola on 2018/7/27.
 */
public class Session {
    private String sessName;
    private String sessKeyWord;
    private String mCacheDir;
    private Context mContext;
    private Thread downloadThread;
    private int sessType;
    private OnDownLoadFinishListener downLoadFinishListener;
    public static final int TYPEKEYWORD=1;
    public static final int TYPEURL=1;
    private Boolean isDownloadFinish=true;
    private DownBaiduPicture db;
    private Runnable download=new Runnable() {
        @Override
        public void run() {
            doSearch(sessKeyWord,mCacheDir,downLoadFinishListener,mContext);
            //下载完成,回调接口更新
            finish();
        }
    };
    public Session(){
    }
    //sess开始运行
    public void start(String sessKeyWord,String mCacheDir,
                      OnDownLoadFinishListener downLoadFinishListener
                        ,Context mContext){
        //sess开始下载
        this.sessKeyWord=sessKeyWord;
        this.mCacheDir=mCacheDir;
        this.downLoadFinishListener=downLoadFinishListener;
        this.mContext=mContext;
        downloadThread=new Thread(download);
        downloadThread.start();
        isDownloadFinish=false;
        System.out.println(sessKeyWord+mContext);

    }
    public void finish(){
        //sess结束下载
        downLoadFinishListener.downloadFinish();
        isDownloadFinish=true;
    }
    public Boolean getDownloadFinish() {
        return isDownloadFinish;
    }

    public static class Builder{
        Session session=new Session();
        //会话名
        public Builder sessName(String sessName){
            session.sessName=sessName;
            return this;
        }
        //设置搜索类型
        public Builder sessType(int sessType){
            session.sessType=sessType;
            return this;
        }
        //创建会话
        public Session create(){
            return session;
        }
    }
    public void cancelDownload(){
        db.setIsContinue(false);
    }
    public void doSearch(String sessKeyWord,String mCacheDir,
                         OnDownLoadFinishListener downLoadFinishListener,Context mContext){
        try {
            db = new DownBaiduPicture(mCacheDir,downLoadFinishListener);
            db.setPicture(sessKeyWord
                    ,1  /**/
                    ,1  /**/
                    ,0  /*宽*/
                    ,0  /*高*/
                    ,20  /*显示几张图片*/
                    ,mContext);
            db.downLoad();
        }catch (IOException e) {

        }
    }
}
