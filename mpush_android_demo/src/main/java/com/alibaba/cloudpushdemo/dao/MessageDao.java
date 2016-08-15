package com.alibaba.cloudpushdemo.dao;

import android.content.Context;
import android.util.Log;

import com.alibaba.cloudpushdemo.component.DatabaseHelper;
import com.alibaba.cloudpushdemo.entitys.MessageEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author: 正纬
 * @since: 15/4/9
 * @version: 1.0
 * @feature: 推送消息DAO
 */
public class MessageDao {

    //数据库相关操作日志tag
    public static final String DB_LOG = "database";

    private Context context;
    private Dao<MessageEntity, Integer> MessageEntityOp;
    private DatabaseHelper helper;

    public MessageDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            MessageEntityOp = helper.getDao(MessageEntity.class);
        } catch (SQLException e) {
            Log.i(DB_LOG, e.toString());
        }
    }

    /**
     * 增加一条数据
     *
     * @param entity
     */
    public void add(MessageEntity entity) {
        try {
            MessageEntityOp.create(entity);
            Log.i(DB_LOG, "Insert : " + entity.toString());
        } catch (SQLException e) {
            Log.i(DB_LOG, e.toString());
        }

    }

    /**
     * 删除单条数据
     *
     * @param entity
     */
    public void del(MessageEntity entity) {
        try {
            MessageEntityOp.delete(entity);
            Log.i(DB_LOG, "Delete : " + entity.toString());
        } catch (SQLException e) {
            Log.i(DB_LOG, e.toString());
        }

    }

    public void delAll() {
        for (MessageEntity entity : this.getAll()) {
            this.del(entity);
        }
    }

    /**
     * 查询全部
     *
     * @return
     */
    public ArrayList<MessageEntity> getAll() {
        try {
            Log.i(DB_LOG, "Query : All");
            return (new ArrayList<MessageEntity>() {{
                this.addAll(MessageEntityOp.queryForAll());
            }});
        } catch (SQLException e) {
            Log.i(DB_LOG, e.toString());
        }
        return null;
    }
}
