package com.alibaba.cloudpushdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.cloudpushdemo.entitys.MessageEntity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author: 正纬
 * @since: 15/4/9
 * @version: 1.1
 * @feature: 消息列表Adapter，自定义消息列表的展示方式
 */
public class MessageListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    ArrayList<MessageEntity> mList;

    public MessageListAdapter(Context context, ArrayList<MessageEntity> paramList) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mList = paramList;
        Collections.reverse(mList);
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

        convertView = inflater.inflate(R.layout.message_list_cell, null);

        TextView messageid = (TextView) convertView.findViewById(R.id.messageid);
        TextView messagetitle = (TextView) convertView.findViewById(R.id.messagetitle);
        TextView messagecontent = (TextView) convertView.findViewById(R.id.messagecontent);
        TextView appid = (TextView) convertView.findViewById(R.id.appid);
        TextView createtime = (TextView) convertView.findViewById(R.id.createtime);

        messageid.setText("MsgID : " + String.valueOf(mList.get(position).getMessageId()));

        String messageTitle = mList.get(position).getMessageTitle();
        String messageContent = mList.get(position).getMessageContent();

        messagetitle.setText(messageTitle.length() < 9 ? messageTitle : messageTitle.substring(0, 8) + "...");
        messagecontent.setText(messageContent.length() < 21 ? messageContent : messageContent.substring(0, 20) + "...");
        appid.setText("AppKey : " + String.valueOf(mList.get(position).getAppId()));
        createtime.setText("时间 : " + mList.get(position).getCreateTime());

        convertView.setEnabled(false);
        return convertView;
    }
}
