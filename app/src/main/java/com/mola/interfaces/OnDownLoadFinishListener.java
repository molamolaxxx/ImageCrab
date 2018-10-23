package com.mola.interfaces;

/**
 * Created by Administrator on 2018/7/25.
 */

public interface OnDownLoadFinishListener {
    //每成功下载一张
    void onFinishOnePage();
    //单张下载超时
    void downloadTimeout();
    void downloadFinish();
}
