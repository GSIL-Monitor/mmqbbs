package com.pojo;

/**
 /**
 *
 * 类名称: MessageTest
 * 类描述: 消息内容实体
 * @author liuyg
 * 创建时间:2017年12月5日上午10:41:40
 */
public class MessageUrl extends BaseMessage {

    private String MsgId;//消息id，64位整型

    private String Title;//url标题

    private String  Description;//	消息描述

    private String Url;//	消息链接


    public MessageUrl(){

    }


    public MessageUrl(String toUserName, String fromUserName,
                      long createTime, String msgType, String title,String url,String description, String msgId) {
        super();
        ToUserName = toUserName;
        FromUserName = fromUserName;
        CreateTime = createTime;
        MsgType = msgType;
        Title = title;
        MsgId = msgId;
        Url = url;
        Description = description;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

}