package com.mola.imagecrab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.media.RatingCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mola.blurbehind.BlurBehind;
import com.mola.interfaces.OnScaleHintListener;
import com.mola.mywidges.ZoomImageView;
import com.mola.objects.Image;
import com.mola.utils.FileCompratorUtils;

import java.io.File;
import java.util.ArrayList;

public class ImageActivity extends FragmentActivity implements View.OnClickListener,OnScaleHintListener{
    private ZoomImageView bigImageView;

    private ArrayList<ZoomImageView> zoomImageViewArrayList;
    private Image im;
    private TextView back;
    private TextView save;
    private TextView changeShowType;

    private int position;
    private FileCompratorUtils fileCompratorUtils;
    private MyImageAdapter myImageAdapter;
    private int currentImageNum;
    private ViewPager mViewPager;
    private String path;
    private ArrayList<String> pathList;
    public static final int TYPE_BIG=1;
    public static final int TYPE_SMALL=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        //获得显示的位置
        position=getIntent().getIntExtra("pos",0);
        path=getIntent().getStringExtra("path");
        //获得当前所有图片总数
        currentImageNum=getIntent().getIntExtra("currentImageNum",0);
        System.out.println("当前图片总数:"+currentImageNum);
        //
        BlurBehind.getInstance()//添加模糊背景
                .withAlpha(100)
                .withFilterColor(getResources().getColor(R.color.pureBlack))
                .setBackground(this);
        initViews();
        initListener();
        fileCompratorUtils=new FileCompratorUtils();
    }
    private void initViews(){
        //加载图片
//        bigImageView=(ZoomImageView) findViewById(R.id.big_image);
//        Bitmap bitmap= BitmapFactory.decodeFile(im.getPath());
//        bigImageView.setImageBitmap(bitmap);
        //
        back=(TextView) findViewById(R.id.back);
        save=(TextView)findViewById(R.id.save_single);
        changeShowType=(TextView)findViewById(R.id.hint_image);
        mViewPager=findViewById(R.id.mViewPager);
        mViewPager.setAdapter(myImageAdapter=new MyImageAdapter(MainActivity.getAllImageInfo(),mViewPager,ImageActivity.this,this));
        mViewPager.setCurrentItem(position);
    }
    private void initListener(){
        back.setOnClickListener(this);
        save.setOnClickListener(this);
        //设置imageview提示改变监听按钮
        //bigImageView.setListener(this);
    }

    @Override
    public void onHintChange(int type) {
        if(type==TYPE_BIG)
            //提示缩小
            changeShowType.setText("双击缩小图片");
        else
            changeShowType.setText("双击放大图片");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
            case R.id.save_single:{
                //保存
                //获得当前页签的位置
                int pos=mViewPager.getCurrentItem();
                String imageCachePath=MainActivity.getBigImage(pos).getPath();
                File file=new File(path);
                if (!file.exists()){
                    file.mkdir();
                }
                if(fileCompratorUtils.copyFile(imageCachePath,path+"/"+System.currentTimeMillis()+".png")) {
                    Toast.makeText(ImageActivity.this, "图片保存成功,保存到"+path, Toast.LENGTH_SHORT).show();
                    // 发广播
                    finish();
                }
                else {
                    Toast.makeText(ImageActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                Intent intent=new Intent("refresh");
                intent.putExtra("position",position);
                sendBroadcast(intent);
                break;
            }
        }
    }


}

