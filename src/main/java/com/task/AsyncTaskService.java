package com.task;

import com.pojo.MmSysGzh;
import com.pojo.MmSysSetting;
import com.pojo.MmSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AsyncTaskService {
    @Autowired
    AsyncTaskExecute async ;
    // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskExecute.class);
    // AsyncTaskExecute async = context.getBean(AsyncTaskExecute.class);
    public void queryCoupons(String FromUserName, MmSysSetting mmSysSetting, MmSysGzh mmSysGzh, String Content, String title, String pid, String description, String url, String picurl){
       async.executeQueryCoupons( FromUserName, mmSysSetting, mmSysGzh,  Content,  title,  pid, description, url, picurl);
    }
    public void SendKfMess(String FromUserName,String gzhName,String wxappid,String wxappsecret,String title,String description,String url,String PIC_URL){
        async.SendKfMess(  FromUserName, gzhName, wxappid, wxappsecret, title, description, url, PIC_URL);
    }
    public void SendKfMessText(String FromUserName,String gzhName,String wxappid,String wxappsecret,String context){
        async.SendKfMessText(  FromUserName, gzhName, wxappid, wxappsecret, context);
    }
    public void sendMessQrcode(MmSysUser mmSysUser, MmSysGzh mmSysGzh, Boolean CreateQrcodeFlag, String QrcodeAppId, String QrcodeAppSecret){
        async.sendMessQrcode( mmSysUser, mmSysGzh, CreateQrcodeFlag, QrcodeAppId, QrcodeAppSecret);
    }
    public void orderGet(String session, Date date,Integer i){
            async.orderGet(session,date,i);
    }
    public void orderCalculation(String session, Date date){
            async.orderCalculation(session,date);
    }
    public void updateMenu(MmSysGzh mmSysGzh, MmSysSetting mmSysSetting){
        async.updateMenu(mmSysGzh,mmSysSetting);
    }
}
