package com.mola.imagecrab;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.mola.blurbehind.BlurBehind;
import com.mola.objects.ImageLib;
import com.mola.popmenu.PopMenu;
import com.mola.popmenu.PopMenuItem;
import com.mola.popmenu.PopMenuItemListener;
import com.mola.swipemenulistview.SwipeMenu;
import com.mola.swipemenulistview.SwipeMenuCreator;
import com.mola.swipemenulistview.SwipeMenuItem;
import com.mola.swipemenulistview.SwipeMenuListView;
import com.mola.utils.BitMapUtils;
import com.mola.utils.MyDrawableUtils;

import java.io.File;
import java.util.ArrayList;

public class FileManageActivity extends FragmentActivity implements View.OnClickListener{
    private ImageView close;
    private ImageView settingMenu;
    private PopMenu popMenu;
    private LinearLayout FileEmptyHint;
    private ImageView add;
    private ArrayList<ImageLib> imgLibs;
    private String mDownLoadDir;
    private SwipeMenuListView mySwipeMenuListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);
        initViews();
        initPopMenu();
        BlurBehind.getInstance()//添加模糊背景
                .withAlpha(200)
                .withFilterColor(getResources().getColor(R.color.white))
                .setBackground(this);
        mDownLoadDir= Environment.getExternalStorageDirectory().getPath()+"/"+"imageCrabDownload";
        getAllImageLib();
        checkEmpty();
        initListView();
    }
    private void checkEmpty(){
        if(imgLibs.size()!=0){
            FileEmptyHint.setVisibility(View.INVISIBLE);
        }
        else {
            FileEmptyHint.setVisibility(View.VISIBLE);
            springEffect();
        }
    }
    public void rotate(){
        Animation rotateAnimation=AnimationUtils.loadAnimation(this,R.anim.add_anim);
        add.startAnimation(rotateAnimation);
    }
    private void initListView(){
        mySwipeMenuListView=findViewById(R.id.folder_lv);
        mySwipeMenuListView.setAdapter(new MyFileManagerAdapter(imgLibs,this));
        //创建左滑菜单属性
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            //创造菜单
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background

                // set item width
                deleteItem.setWidth(MyDrawableUtils.dp2px(100,FileManageActivity.this));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_coffee2go_sm);
                //将删除属性加到菜单里
                menu.addMenuItem(deleteItem);
            }
        };
        mySwipeMenuListView.setMenuCreator(creator);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_manager:{
                finish();
                break;
            }
            case R.id.setting3:{
                popMenu.show();
                break;
            }
            case R.id.manager_empty_hint:{
                springEffect();
                //test
                getAllImageLib();
                break;
            }
            case R.id.add:{
                rotate();
                break;
            }
        }
    }
    //获取图片库的信息

    private void getAllImageLib(){
        imgLibs=new ArrayList<>();
        File rootFile=new File(mDownLoadDir);
        String[] folderList=rootFile.list();
        for(String folderName:folderList){
            String folderPath=mDownLoadDir+"/"+folderName;
            ImageLib imageLib=new ImageLib(folderPath);
            System.out.println(folderPath);
            //获取lib的属性
            //获取名字
            imageLib.setName(folderName);
            //获取图片地址
            File folderFile=new File(folderPath);
            String[] imgList=folderFile.list();
            int imgNum=imgList.length;
            //设置图片个数
            imageLib.setNum(imgNum);
            for(String img:imgList){
                //图片信息
                if(img.equals(imgList[0])){
                    //获取首张图片
                    Bitmap icon=null;
                    //压缩图片
                    icon=BitMapUtils.compressBitMap(folderPath+"/"+img);
                    imageLib.setFirstPic(icon);
                }
                ArrayList<String> imgPathList=new ArrayList<>();
                imgPathList.add(folderPath+"/"+img);
                imageLib.setImgPath(imgPathList);
            }
            imgLibs.add(imageLib);
        }
    }
    private void initPopMenu(){
        int w=500,h=500;
        Drawable db=getResources().getDrawable(R.drawable.head_people);
        Drawable db1=getResources().getDrawable(R.drawable.ic_action_picture);
        Drawable db2=getResources().getDrawable(R.drawable.ic_action_bug_dr);
        Drawable db3=getResources().getDrawable(R.drawable.ic_action_info);
        Drawable db4=getResources().getDrawable(R.drawable.ic_action_more_y);
        db= MyDrawableUtils.zoomDrawable(db,w,h);
        Drawable db0=MyDrawableUtils.zoomDrawable(db,1,1);
        db1=MyDrawableUtils.zoomDrawable(db1,w,h);
        db2=MyDrawableUtils.zoomDrawable(db2,w,h);
        db3=MyDrawableUtils.zoomDrawable(db3,w,h);
        db4=MyDrawableUtils.zoomDrawable(db4,w,h);
        popMenu=new PopMenu.Builder().attachToActivity(FileManageActivity.this)
                .addMenuItem(new PopMenuItem("爬虫设置",db2))
                .addMenuItem(new PopMenuItem("登陆",db))
                .addMenuItem(new PopMenuItem("图库",db1))
                .addMenuItem(new PopMenuItem("关于",db3))
                //空一个
                .addMenuItem(new PopMenuItem("",db0))
                .addMenuItem(new PopMenuItem("更多",db4))
                .setOnItemClickListener(new PopMenuItemListener() {
                    @Override
                    public void onItemClick(PopMenu popMenu, int position) {
                        switch (position){
                            case 0:{
                                Toast.makeText(FileManageActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 1:{
                                //登陆设置

                                break;
                            }
                            case 2:{
                                //图库
                                break;
                            }
                            case 3:{
                                //关于
                                break;
                            }
                            case 5:{
                                //更多
                                break;
                            }
                        }
                    }
                })
                .build();
    }
    private void initViews(){
        close=(ImageView)findViewById(R.id.close_manager);
        close.setOnClickListener(this);
        settingMenu=(ImageView)findViewById(R.id.setting3);
        settingMenu.setOnClickListener(this);
        add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(this);
        FileEmptyHint=(LinearLayout)findViewById(R.id.manager_empty_hint);
        FileEmptyHint.setOnClickListener(this);
    }
    private void springEffect(){
        SpringSystem springSystem=SpringSystem.create();
        Spring spring=springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(60,2));
        spring.addListener(new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                float value=(float) spring.getCurrentValue();
                float scale = 1.5f - (value * 0.5f);
                FileEmptyHint.setScaleX(scale);
                FileEmptyHint.setScaleY(scale);
            }
        });
        spring.setEndValue(1f);
    }
}
