package com.mola.imagecrab;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mola.openingstartanimation.OpeningStartAnimation;
import com.mola.openingstartanimation.RotationDrawStrategy;
import com.mola.utils.MyDrawableUtils;

import java.util.Timer;
import java.util.TimerTask;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        startOpenningAnimation();
        final Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(EnterActivity.this,MainActivity.class));
                timer.cancel();
                finish();
            }
        };
        timer.schedule(timerTask,1920);
    }
    private void startOpenningAnimation(){
        Resources resources=EnterActivity.this.getResources();
        Drawable drawable=resources.getDrawable(R.mipmap.ic_action_bug);
        drawable= MyDrawableUtils.zoomDrawable(drawable,100,100);
        OpeningStartAnimation op=new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new RotationDrawStrategy())
                .setAppStatement("爬到你想要的图片！")
                .setAppName("ImageCrab")
                .setAppIcon(drawable)
                .setAnimationInterval(2000)
                .setAnimationFinishTime(10)
                .create();
        op.show(EnterActivity.this);
    }
}
