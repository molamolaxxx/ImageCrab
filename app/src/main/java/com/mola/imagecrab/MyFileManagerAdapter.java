package com.mola.imagecrab;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mola.objects.ImageLib;
import com.mola.swipemenulistview.BaseSwipListAdapter;
import com.mola.utils.BitMapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/22.
 */

public class MyFileManagerAdapter extends BaseSwipListAdapter {
    private ArrayList<ImageLib> imageLibList;
    private LayoutInflater layoutInflater;
    private Context context;
    public MyFileManagerAdapter(ArrayList<ImageLib> imageLibList, Context context) {
        this.imageLibList=imageLibList;
        layoutInflater=LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount() {
        return imageLibList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageLibList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.manager_item_layout,null);
            FolderViewHolder mHolder=new FolderViewHolder(convertView);
            //给控件赋值
            mHolder.nameText.setText("相册名:"+getAlbumName(imageLibList.get(position).getName()));
            mHolder.numText.setText("图片数:"+imageLibList.get(position).getNum());
            mHolder.iv.setImageBitmap(BitMapUtils.zoomImg(imageLibList.get(position).getFirstPic(),BitMapUtils.dp2px(135,context),BitMapUtils.dp2px(150,context)));
        }
        else {
            FolderViewHolder mHolder=(FolderViewHolder) convertView.getTag();
            mHolder.nameText.setText("相册名:"+getAlbumName(imageLibList.get(position).getName()));
            mHolder.numText.setText("图片数:"+imageLibList.get(position).getNum());
            mHolder.iv.setImageBitmap(BitMapUtils.zoomImg(imageLibList.get(position).getFirstPic(),BitMapUtils.dp2px(135,context),BitMapUtils.dp2px(150,context)));
        }
        return convertView;
    }
    private String getAlbumName(String rawName){
        String parseName;
        if(rawName.length()>4)
            parseName=rawName.substring(0,3)+"...";
        else
            parseName=rawName;
        return parseName;
    }
    class FolderViewHolder{
        TextView numText;
        TextView nameText;
        ImageView iv;
        CardView cv;
        public FolderViewHolder(View v){
            nameText=v.findViewById(R.id.name_text);
            numText=v.findViewById(R.id.num_text);
            iv=v.findViewById(R.id.item_iv);
            cv=v.findViewById(R.id.m_cardView);
            initCardViews();
            v.setTag(this);
        }
        private void initCardViews(){
            //设置圆角
            cv.setRadius(16);
            //设置阴影程度
            cv.setElevation(9);

        }
    }
}
