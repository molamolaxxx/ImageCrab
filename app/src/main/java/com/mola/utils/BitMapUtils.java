package com.mola.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.TypedValue;

/**
 * Created by Administrator on 2018/9/22.
 */

public class BitMapUtils {
    public static Bitmap compressBitMap(String bitMapPath){
        BitmapFactory.Options options=new BitmapFactory.Options();
        Bitmap bp=BitmapFactory.decodeFile(bitMapPath);
        //长宽分别变成原来的四分之一，大小变成十六分之一
        if(bp.getByteCount()>10000000){
            options.inSampleSize=5;
        }
        else if(bp.getByteCount()>5000000){
            options.inSampleSize=3;
        }
        else if(bp.getByteCount()>1000000){
            options.inSampleSize=2;
        }
        else
            options.inSampleSize=1;
        return BitmapFactory.decodeFile(bitMapPath,options);
    }
    public  static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
