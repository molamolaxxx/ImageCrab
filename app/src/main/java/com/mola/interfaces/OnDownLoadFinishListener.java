package com.mola.interfaces;

/**
 * Created by Administrator on 2018/7/25.
 */

public interface OnDownLoadFinishListener {
    //每成功下载一张,number为页号
    void onFinishOnePage(int number,int totalNum);
    //单张下载超时
    void downloadTimeout();
    void downloadFinish();
    void updateProgress(int progress);
    void downloadCanceled();
}
