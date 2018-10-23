package com.mola.imagecrab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mola.interfaces.OnScaleHintListener;
import com.mola.mywidges.ZoomImageView;
import com.mola.objects.Image;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/10.
 */

public class MyImageAdapter extends PagerAdapter {
    private ArrayList<String> pathList;
    private ViewPager viewPager;
    private Context context;
    //实现放大缩小监听器
    private OnScaleHintListener onScaleHintListener;
    public MyImageAdapter(ArrayList<String> pathList, ViewPager viewPager
            , Context context, OnScaleHintListener onScaleHintListener) {
        this.pathList=pathList;
        this.viewPager=viewPager;
        this.context=context;
        this.onScaleHintListener=onScaleHintListener;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //获得容器中imageview,并将对应图片放入图片
        ZoomImageView currentImage=new ZoomImageView(context);
        currentImage.setListener(onScaleHintListener);
        Bitmap bitmap= BitmapFactory.decodeFile(pathList.get(position));
        currentImage.setImageBitmap(bitmap);
        //
        container.removeView(currentImage);
        container.addView(currentImage);
        return currentImage;
    }
    @Override
    public int getCount() {
        return pathList.size();
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        viewPager.removeView((View)object);
    }
}
