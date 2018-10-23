package com.mola.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by molamola on 2018/7/25.
 * 可以快速搭建警告栏
 */

public class MolaQuickBuild {
    MyAlertInterface myAlertInterface;
    AlertDialog mDialog;
    private String title, message, posBtn, negBtn, id;
    public MolaQuickBuild(MyAlertInterface myAlertInterface,  String title, String message, String posBtn, String negBtn, String id) {
        this.myAlertInterface = myAlertInterface;
        this.message = message;
        this.title = title;
        this.posBtn = posBtn;
        this.negBtn = negBtn;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void buildAlert(Context mContext){
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(mContext);
        mDialog = alertBuilder.create();
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, posBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //执行接口中的方法，接口回调
                myAlertInterface.doCommit(id);
            }
        });
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myAlertInterface.doCancel();
            }
        });
        mDialog.show();
    }
}

