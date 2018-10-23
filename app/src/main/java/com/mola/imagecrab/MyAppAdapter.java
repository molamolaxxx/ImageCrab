package com.mola.imagecrab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mola.objects.Image;
import com.mola.swipemenulistview.BaseSwipListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/24.
 */

public class MyAppAdapter extends BaseSwipListAdapter {
    private ArrayList<Image> mImageList;
    private Context context;
    private LayoutInflater layoutInflater;
    public MyAppAdapter(ArrayList<Image> imageList,Context context) {
        mImageList = imageList;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.item_layout, null);
            ViewHolder holder=new ViewHolder(convertView);
            Image im=mImageList.get(position);
            holder.iv.setImageBitmap(im.getBitmap());
            holder.heightAndwidth.setText("像素值:" + im.getWidth() + "*" + im.getHeight());
            holder.size.setText("存储空间:" + im.getSize()/1000 + "kb");
        }
        else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Image im=mImageList.get(position);
            holder.iv.setImageBitmap(im.getBitmap());
            holder.heightAndwidth.setText("像素值:" + im.getWidth() + "*" + im.getHeight());
            holder.size.setText("存储空间:" + im.getSize()/1000 + "kb");
        }
        return convertView;
    }

}
class ViewHolder{
    ImageView iv;
    TextView heightAndwidth;
    TextView size;
    LinearLayout share;
    public ViewHolder(View v){
        iv=(ImageView) v.findViewById(R.id.item_imageview);
        heightAndwidth=(TextView) v.findViewById(R.id.pix_text);
        size=(TextView) v.findViewById(R.id.size_text);
        v.setTag(this);
    }
}
