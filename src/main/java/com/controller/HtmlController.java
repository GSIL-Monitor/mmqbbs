package com.controller;/**
 * 描述：
 * Created by Liuyg on 2018-12-28 9:50
 */

import com.pay.MD5;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-28 09:50
 **/

@Controller
@RequestMapping("/qbb")
public class HtmlController {

    @RequestMapping("base")
    private String base(String req, Model model){
        req = "￥"+req+"￥";
        model.addAttribute("req",req);
        return "base";
    }

    @RequestMapping("testpay")
    @ResponseBody
    private String testpay(){
        MD5 paymd = new MD5();//发送红包类

        //下面是发送红包参数
        String mch_id = "1502746421";//商户号
        String appid = "wx467991903fc7561e";
        String opendid = "oKjlY1DKWtlpgkIXIymx_B3FCpIo"; //发送给指定微信用户的openid
        String send_name = "爱心"; //商户名称
        String Wishing = "恭喜发财"; //祝福语
        String zfKey = "n7658tgJHJkjhghjbBU576GJHBkjhre2";//商户支付API秘钥
        String Client_ip = "122.233.50.127";//服务器ip
        String Act_name = "爱心奖励你";//活动名称
        String Remark = "备注信息";//备注信息
        String totalAmount = "100";//红包金额
        String gzhName = "mmsqb";//公众号name
        //上面面是发送红包参数

        String result = "try报错了";
        try {
            result = paymd.sendRedPack(mch_id,appid,opendid,send_name,Wishing,zfKey,Client_ip,Act_name,Remark,gzhName,totalAmount);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return result;
    }

}
