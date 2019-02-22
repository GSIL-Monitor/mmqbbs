package com.util;/**
 * 描述：
 * Created by Liuyg on 2018-12-26 12:57
 */

import com.pojo.MessageText;
import com.pojo.MessageUrl;
import com.thoughtworks.xstream.XStream;

import java.util.Date;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-26 12:57
 **/

public class TextMessageUtil{
    /**
     * 将发送消息封装成对应的xml格式
     */
    public  String messageToxml(MessageText message) {
        XStream xstream  = new XStream();
        xstream.alias("xml", message.getClass());
        return xstream.toXML(message);
    }

    /**
     * 将发送消息封装成对应的xml格式
     */
    public  String messageToxml(MessageUrl message) {
        XStream xstream  = new XStream();
        xstream.alias("xml", message.getClass());
        return xstream.toXML(message);
    }

    /**
     * 封装发送消息对象,封装时，需要将调换发送者和接收者的关系
     * @param FromUserName
     * @param ToUserName
     */
    public  String initMessage(String FromUserName, String ToUserName,String content) {
        MessageText text = new MessageText();
        text.setToUserName(FromUserName);
        text.setFromUserName(ToUserName);
        text.setContent(content);
        text.setCreateTime(new Date().getTime());
        text.setMsgType("text");
        return  messageToxml(text);
    }
    /**
     * 封装发送消息对象,封装时，需要将调换发送者和接收者的关系
     * @param FromUserName
     * @param ToUserName
     */
    public  String urlMessage(String FromUserName, String ToUserName,String title,String url,String description) {
        MessageUrl text = new MessageUrl();
        text.setToUserName(FromUserName);
        text.setFromUserName(ToUserName);
        text.setCreateTime(new Date().getTime());
        text.setMsgType("link");
        text.setTitle(title);
        text.setUrl(url);
        text.setDescription(description);
        return  messageToxml(text);
    }
}