package com.mola.control;

import android.content.Context;
import android.os.AsyncTask;

import com.mola.interfaces.OnDownLoadFinishListener;

public class DownloadTask extends AsyncTask<Void,Integer,Void> {
    private String sessName;
    private String sessKeyWord;
    private String mCacheDir;
    private Context mContext;
    private OnDownLoadFinishListener downLoadFinishListener;
    private Boolean isDownloadFinish=true;
    private DownBaiduPicture db;
    public DownloadTask(String sessKeyWord, String mCacheDir,
                        OnDownLoadFinishListener downLoadFinishListener, Context mContext){
        //赋值
        this.sessKeyWord=sessKeyWord;
        this.mCacheDir=mCacheDir;
        this.downLoadFinishListener=downLoadFinishListener;
        this.mContext=mContext;
    }

    /**
     *
     * @param voids
     * @return
     * 执行后台下载
     */
    @Override
    protected Void doInBackground(Void... voids) {
        isDownloadFinish=false;
        doSearch(sessKeyWord,mCacheDir,downLoadFinishListener,mContext);
        return null;
    }
    /**
     * 运行结束，回调接口
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        downLoadFinishListener.downloadFinish();
        isDownloadFinish=true;

    }
    /*
    通知进度条更新
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     *
     * @param sessKeyWord
     * @param mCacheDir
     * @param downLoadFinishListener
     * @param mContext
     * 执行搜索
     */
    public void doSearch(String sessKeyWord, String mCacheDir,
                         OnDownLoadFinishListener downLoadFinishListener, Context mContext){
        try {
            db = new DownBaiduPicture(mCacheDir,downLoadFinishListener);
            db.setPicture(sessKeyWord
                    ,1  /**/
                    ,1  /**/
                    ,0  /*宽*/
                    ,0  /*高*/
                    ,20  /*显示几张图片*/
                    ,mContext);
            db.downLoad(this);
            if(isCancelled()) {
                isDownloadFinish=true;
                downLoadFinishListener.downloadCanceled();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDownloadFinished(){
        return isDownloadFinish;
    }
}
