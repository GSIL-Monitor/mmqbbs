package com.controller;/**
 * 描述：
 * Created by Liuyg on 2018-12-26 12:58
 */

import com.pojo.MmSysCopys;
import com.pojo.MmSysGzh;
import com.pojo.MmSysSetting;
import com.pojo.MmSysUser;
import com.service.*;
import com.task.AsyncTaskService;
import com.util.DateUtils;
import com.util.MessageUtil;
import com.util.TextMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-26 12:58
 **/
@Controller
public class MessController {
    @Autowired
    private GzhService gzhService;
    @Autowired
    private UserService userService;
    @Autowired
    private SettingService settingService;
    @Autowired
    @Lazy
    private AsyncTaskService asyncTaskService;
    @Autowired
    private PayService payService;
    @Value("${QrcodeAppId}")
    private String QrcodeAppId;
    @Value("${QrcodeAppSecret}")
    private String QrcodeAppSecret;
    @Value("${CreateQrcodeFlag}")
    private Boolean CreateQrcodeFlag;
    @Autowired
    private CopysService copysService;

    @RequestMapping(value = "{gzhName}/wx",method= RequestMethod.POST)
    public void dopost(HttpServletRequest request, HttpServletResponse response,@PathVariable("gzhName")String gzhName){
        response.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        //将微信请求xml转为map格式，获取所需的参数
        Map<String,String> map = MessageUtil.xmlToMap(request);
        String ToUserName = map.get("ToUserName");
        String FromUserName = map.get("FromUserName");//openID
        String Event = map.get("Event");//事件类型:关注在关注SCAN，关注未关注subscribe
        String EventKey = null;
        if(map.containsKey("EventKey")){//判断map是否带有EventKey参数
            EventKey = map.get("EventKey").replace("qrscene_","");//邀请二维码的参数,一般带入是上级id
        }
        String MsgType = map.get("MsgType");
        String Content = map.get("Content");//消息内容
        MmSysGzh mmSysGzh = gzhService.selectByPrimaryKey(gzhName);
        MmSysUser mmSysUser = userService.selectByOpenid(FromUserName);
        MmSysSetting mmSysSetting = settingService.selectByGzhName(gzhName);
        List<MmSysCopys> copysList = copysService.selectByGzhName(gzhName);
        System.out.println(map.toString());
        //是否回复客服消息
        int flag = 0;
        //是否客服消息参数
        String title = "";
        String description = "";
        String url = "";
        String picurl = "";
        //是否普通消息文字
        String content = mmSysGzh.getGzhnc()+"不明白您在说什么哦，您可以资讯人工客服~";
        TextMessageUtil textMessage = new TextMessageUtil();
        String message = null;

        Integer yhztcw = 0 ;
        boolean yhztcwflag = false ;
        Date newdate = new Date();
        try {
            if (mmSysUser == null) {
                yhztcwflag = true;
                mmSysUser = new MmSysUser();
                mmSysUser.setOpenid(FromUserName);
                mmSysUser.setGzhname(gzhName);
                mmSysUser.setAlreadyMoney(0D);
                mmSysUser.setWatingMoney(0D);
                if ( !StringUtils.isEmpty(EventKey) && "subscribe".equals(Event)  ){
                    MmSysUser levelUser = userService.selectByOpenid(EventKey);
                    mmSysUser.setOpenid1(levelUser.getOpenid());
                    mmSysUser.setOpenid2(levelUser.getOpenid1());
                    mmSysUser.setOpenid3(levelUser.getOpenid2());
                }
                yhztcw = userService.insert(mmSysUser);
                message = textMessage.initMessage(FromUserName, ToUserName, "欢迎您关注"+mmSysGzh.getGzhnc()+"，分享宝贝链接给我，可以帮你查询优惠券哦~");
                try {
                    out = response.getWriter();
                    out.write(message);
                } catch (IOException esa) {
                    esa.printStackTrace();
                }
                out.close();
                return;
            }
            if( !StringUtils.isEmpty(EventKey) && "SCAN".equals(Event) ){
                if("SCAN".equals(Event)){
                    message = textMessage.initMessage(FromUserName, ToUserName, mmSysGzh.getGzhnc()+"欢迎您回来~");
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException esa) {
                        esa.printStackTrace();
                    }
                    out.close();
                    return;
                }
                message = textMessage.initMessage(FromUserName, ToUserName, "您已经是老用户了，只有新用户才能被推荐哦");
                try {
                    out = response.getWriter();
                    out.write(message);
                } catch (IOException esa) {
                    esa.printStackTrace();
                }
                out.close();
                return;
            }
            if (mmSysUser.getPid() == null || DateUtils.getDayByMinusDate(mmSysUser.getPidtime(), newdate) >= 60) {
                yhztcwflag = true;
                yhztcw = userService.upPid(mmSysUser);
            }
            if (yhztcw > 0) {
                mmSysUser = userService.selectByOpenid(FromUserName);
            }
            if (mmSysUser == null || mmSysUser.getPid() == null || DateUtils.getDayByMinusDate(mmSysUser.getPidtime(), newdate) >= 60) {
                message = textMessage.initMessage(FromUserName, ToUserName, "推广位不足，请联系管理员~");
                try {
                    out = response.getWriter();
                    out.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.close();
                return;
            }
        }catch (Exception  e){
            message = textMessage.initMessage(FromUserName, ToUserName, "推广位不足，请联系管理员~");
            try {
                out = response.getWriter();
                out.write(message);
            } catch (IOException es) {
                es.printStackTrace();
            }
            out.close();
            return;
        }

        String pid = mmSysUser.getPid();
        //处理文本类型，实现输入1，回复相应的封装的内容
        if("text".equals(MsgType) || "event".equals(MsgType) ){
            if("event".equals(MsgType)){
                Content = EventKey;
            }
            if( Content.indexOf("https://m.tb.cn") > -1 ){
                flag = 99;
            }
            if( "搜优惠券".equals(Content) ){
                content = "去淘宝复制链接发送给"+mmSysGzh.getGzhnc()+"，"+mmSysGzh.getGzhnc()+"就会帮您查询优惠券呦~";
                flag = 98;
            }
            if( "爱心兑换".equals(Content) ){
                flag = 3;
            }
            if( "我的背包".equals(Content) ){
                mmSysUser.setAlreadyMoney( mmSysUser.getAlreadyMoney() == null ? 0 : mmSysUser.getAlreadyMoney() );
                mmSysUser.setWatingMoney( mmSysUser.getWatingMoney() == null ? 0 : mmSysUser.getWatingMoney() );
                content = "您当前有"+mmSysUser.getAlreadyMoney()+"颗爱心，任务中有"+mmSysUser.getWatingMoney()+"颗爱心~\n回复\"爱心兑换\"可以兑换红包奖励呦";
                flag = 98;
            }
            if( "推荐".equals(Content) ){
                flag = 1;
            }
            if( Content != null && Content.indexOf(mmSysGzh.getXgkey()+"更新菜单") > -1 ){
                flag = 2;
            }
            if( Content != null && Content.indexOf(mmSysGzh.getXgkey()+"新增文案") > -1 ){
                MmSysCopys Copys = new MmSysCopys();
                Copys.setGzhname(gzhName);
                Copys.setKeywords(Content.substring(Content.indexOf("#q")+2,Content.lastIndexOf("#q")));
                Copys.setReply(Content.substring(Content.indexOf("#a")+2,Content.lastIndexOf("#a")));
                Copys.setOpenid(FromUserName);
                copysService.insert(Copys);
                content = "新增成功";
                flag = 98;
            }
            if( Content != null && Content.indexOf(mmSysGzh.getXgkey()+"修改文案") > -1 ){
                MmSysCopys Copys = copysService.seleteByGzhNameAndKeywords(gzhName,Content.substring(Content.indexOf("#q")+2,Content.lastIndexOf("#q")));
                Copys.setGzhname(gzhName);
                Copys.setReply(Content.substring(Content.indexOf("#a")+2,Content.lastIndexOf("#a")));
                Copys.setOpenid(FromUserName);
                copysService.updateByPrimaryKey(Copys);
                content = "修改成功";
                flag = 98;
            }
            if( copysList!= null && copysList.size() > 0 ) {
                for (MmSysCopys mmSysCopys : copysList) {
                    if (mmSysCopys.getKeywords().equals(Content)){
                        content = mmSysCopys.getReply();
                        flag = 98;
                    }
                }
            }
            switch (flag){
                case 1:

                    content = "省钱小课堂开课啦！\n" +
                            "快来邀请你的小伙伴加入你的班级\n" +
                            "你邀请的好友会加入一班\n" +
                            "一班同学再邀请的同学会加入你的二班\n" +
                            "二班同学再邀请的同学会加入你的三班\n" +
                            "一班二班三班的同学每人完成任务后他们都会获得不等量的爱心数量，你也会分别获得他们得到爱心的" +
                            String.format( "%.2f",Double.parseDouble(mmSysSetting.getLevel2().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))+
                            "倍 " +String.format( "%.2f",Double.parseDouble(mmSysSetting.getLevel3().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))+
                            "倍" +String.format( "%.2f",Double.parseDouble(mmSysSetting.getLevel4().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))+
                            "倍\n" +
                            "您当前成功推荐一班："+userService.selectCountByOpenid1(mmSysUser.getOpenid())+"人\n" +
                            "您当前成功推荐二班："+userService.selectCountByOpenid2(mmSysUser.getOpenid())+"人\n" +
                            "您当前成功推荐三班："+userService.selectCountByOpenid3(mmSysUser.getOpenid())+"人\n" +
                           // "推荐1人您将获赠0.5个爱心，您的同学完成任务将会奖励您爱心\n" +
                           //  "一班同学完成任务获得一颗爱心，您将获得"+(Double.parseDouble(mmSysSetting.getLevel2().toString())/100D)+"颗爱心\n" +
                           //  "二班同学完成任务获得一颗爱心，您将获得"+(Double.parseDouble(mmSysSetting.getLevel3().toString())/100D)+"颗爱心\n" +
                           //  "三班同学完成任务获得一颗爱心，您将获得"+(Double.parseDouble(mmSysSetting.getLevel4().toString())/100D)+"颗爱心\n" +
                            "如何完成推荐？\n" +
                            "只要朋友们扫您的专属二维码关注并使用就可以啦~";
                    message = textMessage.initMessage(FromUserName, ToUserName,content);
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    asyncTaskService.sendMessQrcode(mmSysUser,  mmSysGzh, CreateQrcodeFlag, QrcodeAppId, QrcodeAppSecret);
                    break;
                case 2:
                    asyncTaskService.updateMenu(mmSysGzh,mmSysSetting);
                    message = textMessage.initMessage(FromUserName, ToUserName,"更新成功...");
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    break;
                case 3:
                    if( mmSysUser.getAlreadyMoney() == null ){
                        mmSysUser.setAlreadyMoney(0D);
                    }
                    if ( mmSysUser.getAlreadyMoney() > 200 ){
                        message = textMessage.initMessage(FromUserName, ToUserName,"您的爱心超过200颗，请联系人工客服兑换~");
                    }else if( mmSysUser.getAlreadyMoney() < 1 ){
                        message = textMessage.initMessage(FromUserName, ToUserName,"您的爱心不满1颗，无法为您兑换哈~");
                    }else{
                        message = textMessage.initMessage(FromUserName, ToUserName,"正在为您兑换爱心，稍后会以红包方式发放~");
                    }
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    if( mmSysUser.getAlreadyMoney() >= 1 && mmSysUser.getAlreadyMoney() <= 200 ) {
                        payService.sendRedPack(mmSysGzh, mmSysUser);
                    }
                    break;
                case 98:
                    //通用回复文案
                    message = textMessage.initMessage(FromUserName, ToUserName,content);
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    break;
                case 99:
                    //先回复文案，避免微信15秒内调用3次
                    message = textMessage.initMessage(FromUserName, ToUserName,"正在为您查询优惠券,请稍等~");
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    asyncTaskService.queryCoupons(FromUserName, mmSysSetting, mmSysGzh,  Content,  title,  pid, description, url, picurl);
                    break;
                default:
                    //最终回复文案
                    message = textMessage.initMessage(FromUserName, ToUserName,mmSysGzh.getGzhnc()+"不明白您在说什么哦，您可以资讯人工客服~");
                    try {
                        out = response.getWriter();
                        out.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.close();
                    break;
            }


        }

    }



}
