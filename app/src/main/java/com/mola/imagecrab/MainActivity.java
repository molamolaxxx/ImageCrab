package com.mola.imagecrab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.mola.blurbehind.BlurBehind;
import com.mola.blurbehind.OnBlurCompleteListener;
import com.mola.interfaces.OnDownLoadFinishListener;
import com.mola.popmenu.OnPopMenuCloseListener;
import com.mola.myprocessbar.NumberProgressBar;
import com.mola.myprocessbar.OnProgressBarListener;
import com.mola.objects.Session;
import com.mola.popmenu.PopMenu;
import com.mola.popmenu.PopMenuItem;
import com.mola.popmenu.PopMenuItemListener;
import com.mola.utils.InputUtils;
import com.mola.utils.MolaQuickBuild;
import com.mola.utils.MyAlertInterface;
import com.mola.objects.Image;
import com.mola.swipemenulistview.SwipeMenu;
import com.mola.swipemenulistview.SwipeMenuCreator;
import com.mola.swipemenulistview.SwipeMenuItem;
import com.mola.swipemenulistview.SwipeMenuListView;
import com.mola.utils.FileCompratorUtils;
import com.mola.utils.MyDrawableUtils;
import com.mola.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity
        implements View.OnClickListener,MyAlertInterface, OnDownLoadFinishListener
        ,OnProgressBarListener,OnPopMenuCloseListener{
    private ImageView home;
    private ImageView settingMenu;

    private EditText searchEdit;
    private TextView clear;
    private ImageView crab;

    private Boolean isCrabTurnAround=false;
    private Boolean isMenuOpen=false;
    private Boolean isFragmentBroadcastReg=false;
    private Boolean isLogin=false;
    private LinearLayout search;
    private LinearLayout saveAll;
    private LinearLayout deleteAll;
    private LinearLayout emptyHint;
    private LinearLayout progessLinearLayout;
    private Animation rotateAnimation;
    private Animation rotateAnimation2;
    private MyAppAdapter myAppAdapter;
    private SwipeMenuListView sml;
    private static ArrayList<String> pathList;
    private Button CancelDownload;
    private Session session;
    private String content="";
    private  String mCacheDir;
    private String mDownLoadDir;
    private TextView setPx;
    private PopMenu popMenu;
    private TabLayout mTabLayout;

    private AppBarLayout mAppBarLayout;
    private FileCompratorUtils fileCompratorUtils;
    public static ArrayList<Image> imageList;
    private NumberProgressBar numberProgressBar;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //下载完毕
                case 1: {
                    Toast.makeText(MainActivity.this,"全部下载完毕！",Toast.LENGTH_SHORT).show();
                    progessLinearLayout.setVisibility(View.INVISIBLE);

                    numberProgressBar.setProgress(0);
                    break;
                }
                //刷新list
                case 2:{
                    refreshListView();
                    numberProgressBar.incrementProgressBy(3);
                    break;
                }
                default:
            }
        }
    };
    @Override
    public void downloadTimeout() {
        Toast.makeText(MainActivity.this,"图片下载超时!",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建图片缓存机制
        PermissionUtils.verifyStoragePermissions(this);
        createCacheAndDownLoadFolder();
        initSession();
        //初始化图片：测试用
        initImages();
        initListView();
        //初始化控件
        initViews();
        //初始化弹簧菜单
        initPopMenu();
        //设置所有控件的监听器
        setAllListeners();
        //初始化广播
        initBroadcast();
        rotateAnimation=AnimationUtils.loadAnimation(this,R.anim.click_clock_rotate);
        rotateAnimation2=AnimationUtils.loadAnimation(this,R.anim.crab_back);
        fileCompratorUtils=new FileCompratorUtils();

    }
    //alert接口重写
    @Override
    public void doCommit(String id) {
        //执行alert的确定按钮
        switch (id) {
            case "delete_all": {
                deleteAllFiles();
                imageList.clear();
                pathList.clear();
                judgeEmpty();
                searchEdit.setText("");
                Toast.makeText(this, "全部删除完毕！", Toast.LENGTH_SHORT).show();
                break;
            }
            case "save_all":{

                saveAllImage();
                imageList.clear();
                judgeEmpty();
                searchEdit.setText("");
                Toast.makeText(this, "全部保存完毕！保存到"+mDownLoadDir+"/"+content, Toast.LENGTH_SHORT).show();
                //刷新系统图库
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mDownLoadDir+"/"+content))));
                deleteAllFiles();
                break;
            }
            case "cancel_download":{
                session.cancelDownload();
                Toast.makeText(this,"已取消下载", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //弹簧菜单关闭监听
    @Override
    public void onClose() {
        if(isMenuOpen){
            isMenuOpen=false;
        }
    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current==max) {
            System.out.println("finish!");
            numberProgressBar.setProgress(0);
        }
    }

    @Override
    public void doCancel() {
        //执行alert的取消按钮
        System.out.println("cancel");
    }
    //下载监听接口重写,每下载一张调用一次
    @Override
    public void onFinishOnePage() {
        //刷新listview
        System.out.println("finish one page!");
        getImages();
        Message message=handler.obtainMessage();
        message.what=2;
        handler.sendMessage(message);
    }
    @Override
    public void downloadFinish() {
        Message message=handler.obtainMessage();
        message.what=1;
        handler.sendMessage(message);
        //判断是否为空
    }
    //刷新listview
    public void refreshListView(){
        //判断list是否为空
        judgeEmpty();
        myAppAdapter.notifyDataSetChanged();
        //将listview滑到最底部
        sml.setSelection(imageList.size());
        //scrollMyListViewToBottom();
    }
    private void scrollMyListViewToBottom() {
        sml.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                sml.setSelection(myAppAdapter.getCount() - 1);
            }
        });

    }
    //返回当下已有图片数
    private int getCurrentImageNum(){
        return imageList.size();
    }
    //保存单张图片
    public Boolean saveOneImage(int position){
        String imageCachePath=imageList.get(position).getPath();
        String imageSavefolder=mDownLoadDir+"/"+content;
        File file=new File(imageSavefolder);
        if (!file.exists()){
            file.mkdir();
        }
        if(fileCompratorUtils.copyFile(imageCachePath,imageSavefolder+"/"+System.currentTimeMillis()+".png")) {
            return true;
        }
        else {
            Toast.makeText(MainActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        //刷新相册

    }
    //保存所有图片
    public void saveAllImage(){
        while (imageList.size()!=0){
            saveOneImage(0);
            imageList.remove(0);
            pathList.remove(0);
        }
    }
    private void initTabLayout(){
        mTabLayout=findViewById(R.id.tab_layout);
        //设置下划线颜色
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        //设置背景颜色和选择色
        mTabLayout.setTabTextColors(getResources().getColor(R.color.tabBackColor),getResources().getColor(R.color.pureWhite));
        mTabLayout.addTab(mTabLayout.newTab().setText("第一页"));
        mTabLayout.addTab(mTabLayout.newTab().setText("第二页"));
        mTabLayout.addTab(mTabLayout.newTab().setText("第三页"));
    }
    private void initPopMenu(){
        int w=500,h=500;
        Drawable db=getResources().getDrawable(R.drawable.head_people);
        Drawable db1=getResources().getDrawable(R.drawable.ic_action_picture);
        Drawable db2=getResources().getDrawable(R.drawable.ic_action_bug_dr);
        Drawable db3=getResources().getDrawable(R.drawable.ic_action_info);
        Drawable db4=getResources().getDrawable(R.drawable.ic_action_more_y);
        db=MyDrawableUtils.zoomDrawable(db,w,h);
        Drawable db0=MyDrawableUtils.zoomDrawable(db,1,1);
        db1=MyDrawableUtils.zoomDrawable(db1,w,h);
        db2=MyDrawableUtils.zoomDrawable(db2,w,h);
        db3=MyDrawableUtils.zoomDrawable(db3,w,h);
        db4=MyDrawableUtils.zoomDrawable(db4,w,h);
        popMenu=new PopMenu.Builder().attachToActivity(MainActivity.this)
                .setCloseListener(this)
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
                                //爬虫设置
                                if(!isLogin){
                                    Toast.makeText(MainActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case 1:{
                                //登陆设置
                                Timer tm=new Timer();
                                tm.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainActivity.this,WebContentActivity.class);
                                        intent.putExtra("web_type",1);
                                        startActivity(intent);
                                    }
                                },200);
                                break;
                            }
                            case 2:{
                                //图库
                                BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                                    @Override
                                    public void onBlurComplete() {
                                        Intent intent=new Intent(MainActivity.this,FileManageActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            }
                            case 3:{
                                //关于
                                Timer tm=new Timer();
                                tm.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainActivity.this,WebContentActivity.class);
                                        intent.putExtra("web_type",3);
                                        startActivity(intent);
                                    }
                                },200);
                                break;
                            }
                            case 5:{
                                //更多
                                Timer tm=new Timer();
                                tm.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainActivity.this,WebContentActivity.class);
                                        intent.putExtra("web_type",5);
                                        startActivity(intent);
                                    }
                                },200);
                                break;
                            }
                        }
                    }
                })
                .build();
    }
    public void initImages(){
        imageList=new ArrayList<>();
        pathList=new ArrayList<>();
    }
    public void initSession(){
        session=new Session.Builder().sessName("new sess")
                .sessType(Session.TYPEKEYWORD)
                .create();
    }
    public static Image getBigImage(int pos){
        return imageList.get(pos);
    }
    public static ArrayList<String> getAllImageInfo(){
        ArrayList<String> temp=new ArrayList<>();
        temp=(ArrayList<String>) pathList.clone();

        return temp;
    }
    public void getImages(){
        //获得最后一张图片
        Image im=fileCompratorUtils.getLatestImage(mCacheDir+"/"+content);
        if(im.getPath().equals("1"))
            return;
        imageList.add(im);
        pathList.add(im.getPath());
//        for(Image im:imageList){
//            System.out.print(im.getPath()+"    ");
//            System.out.println(im.getSize()/1000+"kb");
//        }
    }
    public void initListView(){
        sml=(SwipeMenuListView) findViewById(R.id.lv);
        myAppAdapter=new MyAppAdapter(imageList,this);
        sml.setAdapter(myAppAdapter);
        //创建左滑菜单属性
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            //创造菜单
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // 设置其背景颜色
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                // 设置宽度
                openItem.setWidth(MyDrawableUtils.dp2px(90,MainActivity.this));
                // set item title
                // set item title fontsize
                openItem.setIcon(R.drawable.ic_action_save_sm);

                // 将打开属性加到菜单里
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.blue)));
                // set item width
                deleteItem.setWidth(MyDrawableUtils.dp2px(90,MainActivity.this));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_coffee2go_sm);
                //将删除属性加到菜单里
                menu.addMenuItem(deleteItem);
            }
        };
        sml.setMenuCreator(creator);
        //设置滑动监听器
//        sml.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//
//            @Override
//            public void onSwipeStart(int position) {
//                // swipe start
//            }
//
//            @Override
//            public void onSwipeEnd(int position) {
//                // swipe end
//            }
//        });
        sml.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:{
                        if (saveOneImage(position)) {
                            imageList.remove(position);
                            pathList.remove(position);
                            //如果图片集为空
                            if (imageList.size() == 0) {
                                emptyHint.setVisibility(View.VISIBLE);
                                springEffect(emptyHint);
                                mAppBarLayout.setExpanded(false);
                            }
                            myAppAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(MainActivity.this, "图片保存到" + mDownLoadDir+"/"+content, Toast.LENGTH_SHORT).show();
                        //刷新系统图库
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mDownLoadDir+"/"+content))));
                        break;
                    }
                    case 1:{
                        imageList.remove(position);
                        pathList.remove(position);
                        //如果图片集为空
                        if(imageList.size()==0) {
                            emptyHint.setVisibility(View.VISIBLE);
                            springEffect(emptyHint);
                            mAppBarLayout.setExpanded(false);
                        }
                        myAppAdapter.notifyDataSetChanged();
                        //删除刷新listview
                        break;
                    }
                }
                return false;
            }
        });
        // set MenuStateChangeListener
//        sml.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
//            @Override
//            public void onMenuOpen(int position) {
//            }
//
//            @Override
//            public void onMenuClose(int position) {
//            }
//        });
        sml.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                //使下一个activity高斯模糊
                BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent=new Intent(MainActivity.this,ImageActivity.class);
                        intent.putExtra("pos",position);
                        intent.putExtra("path",mDownLoadDir+"/"+content);
                        intent.putExtra("currentImageNum",getCurrentImageNum());
                        startActivity(intent);
                        System.out.println(position);
                    }
                });
            }
        });
    }
    private void createCacheAndDownLoadFolder(){
        String rootDirectory= Environment.getExternalStorageDirectory().getPath();
        //缓存文件夹
        mCacheDir=rootDirectory+"/"+"com.mola.ImageCrab";
        //下载文件夹
        mDownLoadDir=rootDirectory+"/"+"imageCrabDownload";
        File file=new File(mCacheDir);
        File file1=new File(mDownLoadDir);
        if(!file.exists()){
            file.mkdir();
        }
        if(!file1.exists()){
            file1.mkdir();
        }
        //删除所有文件
        FileCompratorUtils.delAllFile(mCacheDir);
        Toast.makeText(this,"删除完毕！",Toast.LENGTH_SHORT).show();
    }
    //判断cache中是否存在文件
    private void judgeEmpty(){
        if(imageList.size()==0){

            myAppAdapter.notifyDataSetChanged();
            emptyHint.setVisibility(View.VISIBLE);
            progessLinearLayout.setVisibility(View.INVISIBLE);
            springEffect(emptyHint);
            mAppBarLayout.setExpanded(false);
        }
        else {
            if(imageList.size()==1)
                springEffect(progessLinearLayout);
            System.out.println("imageList大小不为0");
            emptyHint.setVisibility(View.INVISIBLE);
            progessLinearLayout.setVisibility(View.VISIBLE);
            mAppBarLayout.setExpanded(true);
        }
    }
    private void springEffect(final View v){
        SpringSystem springSystem=SpringSystem.create();
        Spring spring=springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(60,2));
        spring.addListener(new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                float value=(float) spring.getCurrentValue();
                float scale = 1.5f - (value * 0.5f);
                v.setScaleX(scale);
                v.setScaleY(scale);
            }
        });
        spring.setEndValue(1f);
        System.out.println(spring.getStartValue());
    }
    private void initViews(){
        home=(ImageView) findViewById(R.id.return_to_home);
        settingMenu=(ImageView) findViewById(R.id.setting);
        searchEdit=(EditText)findViewById(R.id.search_edit);
        setPx=findViewById(R.id.set_px);
        CancelDownload=(Button) findViewById(R.id.cancel_download);
        progessLinearLayout=(LinearLayout) findViewById(R.id.progress_bar_layout);
        progessLinearLayout.setVisibility(View.INVISIBLE);
        crab=(ImageView) findViewById(R.id.crab);
        numberProgressBar=(NumberProgressBar) findViewById(R.id.number_progress_bar);
        numberProgressBar.setProgressTextColor(getResources().getColor(R.color.white));
        numberProgressBar.setUnreachedBarColor(getResources().getColor(R.color.white));
        numberProgressBar.setReachedBarColor(getResources().getColor(R.color.blue));
        emptyHint=(LinearLayout) findViewById(R.id.empty_hint);

        mAppBarLayout=findViewById(R.id.app_bar);
        mAppBarLayout.setExpanded(false);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!searchEdit.getText().toString().equals("")) {
                    if(!isCrabTurnAround) {
                        crab.setAnimation(rotateAnimation);
                        crab.startAnimation(rotateAnimation);
                        isCrabTurnAround=true;
                    }
                    clear.setVisibility(View.VISIBLE);
                }
                else {
                    if(isCrabTurnAround) {
                        crab.setAnimation(rotateAnimation2);
                        crab.startAnimation(rotateAnimation2);
                        isCrabTurnAround=false;
                    }
                    clear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        clear=(TextView) findViewById(R.id.clear);
        clear.setVisibility(View.INVISIBLE);
        search=(LinearLayout) findViewById(R.id.search);
        saveAll=(LinearLayout) findViewById(R.id.save_all_images);
        deleteAll=(LinearLayout) findViewById(R.id.delete_all_images);
        initTabLayout();
    }
    private void setAllListeners(){
        home.setOnClickListener(this);
        settingMenu.setOnClickListener(this);
        searchEdit.setOnClickListener(this);
        CancelDownload.setOnClickListener(this);
        setPx.setOnClickListener(this);
        clear.setOnClickListener(this);
        search.setOnClickListener(this);
        emptyHint.setOnClickListener(this);
        saveAll.setOnClickListener(this);
        deleteAll.setOnClickListener(this);
        numberProgressBar.setOnProgressBarListener(this);
    }
    private void deleteAllFiles(){
        FileCompratorUtils.delFolder(mCacheDir+"/"+content);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_to_home:{
                doReturnToHome();
                Toast.makeText(this,"进入后台",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.setting:{
                //弹出设置框图
                if (!isMenuOpen) {
                    isMenuOpen=true;
                    popMenu.show();
                }
                else {
                    isMenuOpen=false;
                    popMenu.hide();
                }
                break;
            }
            case R.id.set_px:{
                //设置爬取得像素组

            }
            case R.id.search:{
                if (searchEdit.getText().toString().equals("")) {
                    Toast.makeText(this,"请输入点东西",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    InputUtils.closeInputWriter(MainActivity.this);
                    if(session.getDownloadFinish()) {
                        imageList.clear();
                        pathList.clear();
                        if(!content.equals(""))
                            deleteAllFiles();
                    }
                    else {
                        Toast.makeText(this,"上一个下载还未完成！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                content=searchEdit.getText().toString();
                Toast.makeText(this,"开始搜索！",Toast.LENGTH_SHORT).show();
                session.start(content,mCacheDir,this,MainActivity.this);
                break;
            }
            case R.id.cancel_download:{
                //取消下载
                MolaQuickBuild molaQuickBuild=new MolaQuickBuild(this
                ,"ImageCrab警报","真的要取消下载吗?","取消下载","返回","cancel_download");
                molaQuickBuild.buildAlert(MainActivity.this);
                break;
            }
            case R.id.clear:{
                searchEdit.setText("");
                break;
            }
            case R.id.save_all_images:{
                doSaveAll();
                break;
            }
            case R.id.delete_all_images:{
                dodelete();
                break;
            }
            case R.id.empty_hint:{
                springEffect(emptyHint);
            }
        }
    }
    //返回home页面
    public void doReturnToHome(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    public void doSaveAll(){
        if(imageList.size()!=0) {
            MolaQuickBuild mqb = new MolaQuickBuild(this, "ImageCrab警报", "真的要全部保存吗", "保存", "不保存", "save_all");
            mqb.buildAlert(MainActivity.this);
        }
        else {
            springEffect(emptyHint);
            Toast.makeText(this, "没东西可保存", Toast.LENGTH_SHORT).show();
        }
    }
    public void dodelete(){
        //真香警告
        //开始删除
        if(imageList.size()!=0) {
            MolaQuickBuild mqb = new MolaQuickBuild(this, "ImageCrab警报", "真的要全部删除吗", "删除", "不删除", "delete_all");
            mqb.buildAlert(MainActivity.this);
        }
        else {
            springEffect(emptyHint);
            Toast.makeText(this, "没东西可删", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void initBroadcast(){
        if (!isFragmentBroadcastReg)
        {
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction("refresh");
            BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals("refresh")) {
                        //TODO
                        imageList.remove(intent.getIntExtra("position",0));
                        pathList.remove(intent.getIntExtra("position",0));
                        myAppAdapter.notifyDataSetChanged();
                        if(imageList.size()==0){
                            emptyHint.setVisibility(View.VISIBLE);
                            springEffect(emptyHint);
                        }
                    }
                }
            };
            isFragmentBroadcastReg=true;
            registerReceiver(broadcastReceiver,intentFilter);
        }
    }
    @Override
    protected void onResume() {
        searchEdit.clearFocus();
        System.out.println("refresh");
        springEffect(emptyHint);
        super.onResume();
    }
}
