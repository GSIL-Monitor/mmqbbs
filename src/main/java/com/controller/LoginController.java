package com.controller;/**
 * 描述：
 * Created by Liuyg on 2018-12-26 12:35
 */

import com.alibaba.druid.util.StringUtils;
import com.pojo.MmSysGzh;
import com.pojo.MmSysGzhToken;
import com.service.GzhService;
import com.util.SHA1;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-26 12:35
 **/

@Slf4j
@Controller
public class LoginController {
    @Autowired
    private GzhService gzhService;

    @RequestMapping(value = "{name}/wx",method= RequestMethod.GET)
    public void login(HttpServletRequest request, HttpServletResponse response, @PathVariable("name")String name){
            System.out.println(name);
            System.out.println("success");
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            PrintWriter out = null;
            MmSysGzh mmSysGzh = gzhService.selectByPrimaryKey(name);
            try {
                out = response.getWriter();
                if(checkSignature(signature, timestamp, nonce , mmSysGzh.getWxtoken())){
                    out.write(echostr);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                out.close();
            }
    }
    public boolean checkSignature(String signature,String timestamp,String nonce,String token){
        String[] str = new String[]{token,timestamp,nonce};
        //排序
        Arrays.sort(str);
        //拼接字符串
        StringBuffer buffer = new StringBuffer();
        for(int i =0 ;i<str.length;i++){
            buffer.append(str[i]);
        }
        //进行sha1加密
        String temp = SHA1.encode(buffer.toString());
        //与微信提供的signature进行匹对
        return signature.equals(temp);
    }

    /**
     * 获取token
     *
     * @return
     */
    public JSONObject getAccess_token(String gzhname, String appId, String appSecret) {
        MmSysGzhToken mmSysGzhToken = gzhService.selectGzhToken(gzhname);
        Date date = new Date();
        if( mmSysGzhToken != null && !StringUtils.isEmpty(mmSysGzhToken.getAccesstoken()) ){
            if( ( date.getTime() - mmSysGzhToken.getTime().getTime() ) < (1000*60*100) ) {
                return JSONObject.fromObject(mmSysGzhToken.getAccesstoken());
            }
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        JSONObject jsonObj = null;
        String accessToken = null;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            accessToken = new String(jsonBytes, StandardCharsets.UTF_8);
            log.info(accessToken);
            mmSysGzhToken.setAccesstoken(accessToken);
            mmSysGzhToken.setTime(date);
            gzhService.updateToken(mmSysGzhToken);
            jsonObj = JSONObject.fromObject(accessToken);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }


}
