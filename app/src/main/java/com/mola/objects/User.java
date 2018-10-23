package com.mola.objects;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/28.
 */

public class User {
    //基本属性
    private int id;
    private String userName;
    private String password;
    //功能属性
    //最大爬载数目
    private int maxPic;
    //用户等级
    private int level;
    //用户经验
    private int exp;
    private ArrayList<String> searchHistory;

    public User(String userName) {
        this.userName = userName;
        getMaxPic();
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        getMaxPic();
    }
    private void getMaxPic(){
        switch (level){
            case 1:maxPic=30;break;
            case 2:maxPic=35;break;
            case 3:maxPic=40;break;
            case 4:maxPic=45;break;
            case 5:maxPic=50;break;
            case 6:maxPic=60;break;
            case 7:maxPic=70;break;
            case 8:maxPic=80;break;
            case 9:maxPic=90;break;
            case 10:maxPic=100;break;
        }
    }
    public void addExp(int exp){
        this.exp=this.exp+exp;
        if(isLevelUp()&&level<=10){
            level+=1;
        }
    }
    private Boolean isLevelUp(){
        return false;
    }
}
