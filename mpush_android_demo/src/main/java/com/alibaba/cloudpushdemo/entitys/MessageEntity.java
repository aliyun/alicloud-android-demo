package com.alibaba.cloudpushdemo.entitys;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author: 正纬
 * @since: 15/4/9
 * @version: 1.0
 * @feature: 推送消息实体
 */

@DatabaseTable(tableName = "tb_messages")
public class MessageEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    /**
     * 消息ID
     */
    @DatabaseField
//    private long messageId;
    private String messageId;

    /**
     * 应用ID
     */
    @DatabaseField
    private int appId;

    /**
     * 创建时间
     */
    @DatabaseField
    private String createTime;

    /**
     * 更新时间
     */
    @DatabaseField
    private String modifyTime;

    /**
     * 显式版本号，用于乐观锁
     */
    @DatabaseField
    private int version;

    /**
     * 消息抬头
     */
    @DatabaseField
    private String messageTitle;

    /**
     * 消息内容
     */
    @DatabaseField
    private String messageContent;

    /**
     * 准备打开的目标页面：Activity
     */
    @DatabaseField
    private String targetActivity;

    /**
     * 准备打开的URL
     */
    @DatabaseField
    private String targetURL;

    /**
     * 预留的其他参数位置
     */
    @DatabaseField

    private String otherTips;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    public String getOtherTips() {
        return otherTips;
    }

    public void setOtherTips(String otherTips) {
        this.otherTips = otherTips;
    }

    /**
     * 预留显式无参构造方法 4 OrmLite 反射用
     */
    public MessageEntity(){}

    /**
     * 有参构造方法用于持久化推送消息的时候省点事
     * @param messageid
     * @param appid
     * @param createTime
     * @param messageTitle
     * @param messageContent
     */
    public MessageEntity(String messageid, int appid, String messageTitle, String messageContent, String createTime ) {
        this.messageId = messageid;
        this.appId = appid;
        this.createTime = createTime;
        this.messageTitle = messageTitle;
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", appId=" + appId +
                ", createTime='" + createTime + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", version=" + version +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", targetActivity='" + targetActivity + '\'' +
                ", targetURL='" + targetURL + '\'' +
                ", otherTips='" + otherTips + '\'' +
                '}';
    }
}
