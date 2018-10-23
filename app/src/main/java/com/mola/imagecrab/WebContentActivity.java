package com.mola.imagecrab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.mola.blurbehind.BlurBehind;
import com.mola.blurbehind.OnBlurCompleteListener;
import com.mola.popmenu.PopMenu;
import com.mola.popmenu.PopMenuItem;
import com.mola.popmenu.PopMenuItemListener;
import com.mola.utils.MyDrawableUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WebContentActivity extends FragmentActivity implements View.OnClickListener{
    //安放登陆、信息完善、关于、更多的内容
    private WebView mWebView;
    private ImageView close;
    private ImageView settingMenu;
    private PopMenu popMenu;
    private int webType;
    private FrameLayout webLayout;
    private LinearLayout webEmptyHint;
    private TextView loadingText;
    private SwipeRefreshLayout swipeRefreshLayout;
    //服务器所在ip和端口
    private String ip="http://192.168.1.124:8080";

    private Timer timer;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:loadingText.setText("加载中");break;
                case 1:loadingText.setText("加载中.");break;
                case 2:loadingText.setText("加载中..");break;
                case 3:loadingText.setText("加载中...");break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        //
        webLayout=(FrameLayout)findViewById(R.id.web_view_layout);
        mWebView=(WebView)findViewById(R.id.web_view);
//        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mWebView.setLayoutParams(layoutParams);
//        webLayout.addView(mWebView);
        //网页设置
        webViewSettings();

        webType=getIntent().getIntExtra("web_type",0);
        //加载h5页面
        loadHtml();
        initViews();
        initPopMenu();
        springEffect();
    }
    public void rotate(){

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:{
                finish();
                break;
            }
            case R.id.setting2:{
                popMenu.show();
                break;
            }
            case R.id.web_empty_hint:{
                springEffect();
                break;
            }
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
        popMenu=new PopMenu.Builder().attachToActivity(WebContentActivity.this)
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
                                    Toast.makeText(WebContentActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 1:{
                                //登陆设置
                                if(webType==1)
                                    Toast.makeText(WebContentActivity.this, "正在进行登录", Toast.LENGTH_SHORT).show();
                                else {
                                    webType=1;
                                    mWebView.loadUrl(ip+"/one/login.html");
                                }
                                break;
                            }
                            case 2:{
                                //图库
                                BlurBehind.getInstance().execute(WebContentActivity.this, new OnBlurCompleteListener() {
                                    @Override
                                    public void onBlurComplete() {
                                        Intent intent=new Intent(WebContentActivity.this,FileManageActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            }
                            case 3:{
                                //关于
                                if(webType!=3){
                                    //不在关于页面
                                    webType=3;
                                    mWebView.loadUrl(ip+"/one/about.html");
                                }
                                else
                                    Toast.makeText(WebContentActivity.this, "已处在该页面!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 5:{
                                //更多
                                if(webType!=5){
                                    //不在关于页面
                                    webType=5;
                                    mWebView.loadUrl(ip+"/one/more.html");
                                }
                                else
                                    Toast.makeText(WebContentActivity.this, "已处在该页面!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                })
                .build();
    }
    private void loadHtml(){
        switch (webType){
            case 1://登陆
                mWebView.loadUrl(ip+"/one/login.html");break;
            case 3://关于
                mWebView.loadUrl(ip+"/one/about.html");break;
            case 5://更多
                mWebView.loadUrl(ip+"/one/more.html");break;
        }
        mWebView.setWebViewClient(webViewClient);
    }
    private void initViews(){
        close=(ImageView)findViewById(R.id.close);
        close.setOnClickListener(this);
        settingMenu=(ImageView)findViewById(R.id.setting2);
        settingMenu.setOnClickListener(this);
        webEmptyHint=(LinearLayout)findViewById(R.id.web_empty_hint);
        webEmptyHint.setOnClickListener(this);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setEnabled(false);
        //
        loadingText=(TextView) findViewById(R.id.loading_text);
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
                webEmptyHint.setScaleX(scale);
                webEmptyHint.setScaleY(scale);
            }
        });
        spring.setEndValue(1f);
    }
    private void webViewSettings(){
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
    }
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d("err", "错误ssl！");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.d("err", "错误！");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //页面开始加载
            webEmptyHint.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            //每600s闪烁
            timer=new Timer();
            timer.schedule(new TimerTask() {
                int i = 0;
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = i;
                    handler.sendMessage(message);
                    i=(i+1)%4;
                }
            },0,600);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //页面结束加载
            webEmptyHint.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            //
            timer.cancel();
            timer=null;
            System.gc();
        }
    };
}
