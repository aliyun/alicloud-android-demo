package com.alibaba.cloudpushdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.cloudpushdemo.R;

import java.util.ArrayList;

/**
 * @author: 正纬
 * @since: 15/4/12
 * @version: 1.0
 * @feature: 用于自定义检查中心的列表样式
 */
public class CheckerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    ArrayList<String> mList;

    public CheckerListAdapter(Context context, ArrayList<String> paramList) {
        this.mContext = context;
        this.mList = paramList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.checker_list_cell, null);
        TextView checkerList = (TextView) convertView.findViewById(R.id.checkerContent);

        checkerList.setText("0x0" + position + " : " + mList.get(position));

        convertView.setEnabled(false);
        return convertView;
    }
}
