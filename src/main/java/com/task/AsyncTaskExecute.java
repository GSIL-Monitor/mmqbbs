package com.task;

import com.controller.LoginController;
import com.controller.TbkController;
import com.controller.wechat;
import com.dao.MmSysQrcodeMapper;
import com.pojo.*;
import com.qrcode.CreateQrcode;
import com.service.OrdersService;
import com.service.UserService;
import com.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@EnableAsync
public class AsyncTaskExecute {


    @Value("${TklZhUrl}")
    private String TklZhUrl;
    @Value("${secret}")
    private String secret;//校验秘钥

    @Autowired
    private LoginController loginController;

    @Autowired
    @Lazy
    private TbkController tbkController;
    @Autowired
    @Lazy
    private MmSysQrcodeMapper mmSysQrcodeMapper;
    @Autowired
    @Lazy
    private OrdersService ordersService;
    @Autowired
    @Lazy
    private UserService userService;
    @Value("${KoussUrl}")
    private String KoussUrl;

    /**
     * 异步查询优惠券进行推送
     * @param
     */
    @Async("myExecutor")
    public void executeQueryCoupons(String FromUserName,MmSysSetting mmSysSetting,MmSysGzh mmSysGzh, String Content, String title, String pid,String description,String url,String picurl){
        log.info("异步线程查询优惠券");
        Map<String,String> result = tbkController.TklToTkl(Content, mmSysGzh.getTbappkey(), mmSysGzh.getTbsecret(),mmSysGzh.getTbpid(), pid,mmSysGzh.getTbsession());
        try {
            title = result.get("title");
            String yhj =  result.get("coupon_info") ;
            if(StringUtils.isEmpty(yhj)){
                yhj = "0" ;
            }else{
                yhj = yhj.substring(yhj.indexOf("元减")+2);
                yhj = yhj.substring(0,yhj.indexOf("元"));
            }
            Double yhjje = Double.parseDouble(yhj);
            Double jhj = Double.parseDouble(result.get("zk_final_price")) - yhjje;
            Double yf = jhj / 10000 * Integer.parseInt(result.get("commission_rate")) / 100 * mmSysSetting.getLevel1();
            String tkl = result.get("ulandtkl");
            NumberFormat ddf1= NumberFormat.getNumberInstance() ;
            ddf1.setMaximumFractionDigits(2);
            description = "优惠券面额："+yhj+",卷后爱心："+jhj+",大约奖励爱心："+ddf1.format(yf);
            picurl = result.get("picurl");
            String linkString = "req="+tkl.replace("￥","");
            url = TklZhUrl + "/qbb/base?"+linkString+"&sign"+DigestUtils.md5Hex(linkString + secret);
            SendKfMess(FromUserName,mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),title,description,url,picurl);
        }catch (Exception e){
            SendKfMessText(FromUserName,mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),"暂无优惠券~");
        }
    }

    /**
    * @Description:  发送客服消息
    * @Param: [FromUserName, gzhName, wxappid, wxappsecret, title, description, url, PIC_URL]
    * @return: void
    * @Author: Liuyg
    * @Date: 2018/12/30
    */
    @Async("myExecutor")
    public void SendKfMess(String FromUserName,String gzhName,String wxappid,String wxappsecret,String title,String description,String url,String PIC_URL){
        log.info("异步发送客服消息");
        JSONObject text = new JSONObject();
        JSONObject context = new JSONObject();
        context.element("title",title);
        context.element("description",description);
        context.element("url",url);
        context.element("picurl",PIC_URL);
        text.element("articles", "["+context+"]");
        JSONObject json = new JSONObject();
        json.element("touser", FromUserName);
        json.element("news", text);
        json.element("msgtype", "news");
        wechat.customSend(json, loginController.getAccess_token(gzhName,wxappid,wxappsecret).getString("access_token"));
    }

    /**
    * @Description:  发送客服消息文字
    * @Param: [FromUserName, gzhName, wxappid, wxappsecret, title]
    * @return: void
    * @Author: Liuyg
    * @Date: 2018/12/30
    */
    @Async("myExecutor")
    public void SendKfMessText(String FromUserName,String gzhName,String wxappid,String wxappsecret,String title){
        log.info("异步发送客服消息文字");
        JSONObject context = new JSONObject();
        context.element("content",title);
        JSONObject json = new JSONObject();
        json.element("touser", FromUserName);
        json.element("text", context);
        json.element("msgtype", "text");
        wechat.customSend(json, loginController.getAccess_token(gzhName,wxappid,wxappsecret).getString("access_token"));
    }

    /**
     * @Description:  发送客服消息图片
     * @Param: [FromUserName, gzhName, wxappid, wxappsecret, title]
     * @return: void
     * @Author: Liuyg
     * @Date: 2018/12/30
     */
    @Async("myExecutor")
    public void SendKfMessImage(String FromUserName,String gzhName,String wxappid,String wxappsecret,String media_id){
        log.info("异步发送客服消息图片");
        JSONObject image = new JSONObject();
        image.element("media_id",media_id);
        JSONObject json = new JSONObject();
        json.element("touser", FromUserName);
        json.element("image", image);
        json.element("msgtype", "image");
        wechat.customSend(json, loginController.getAccess_token(gzhName,wxappid,wxappsecret).getString("access_token"));
    }

    @Async("myExecutor")
    public void orderGet(String session, Date date,Integer i){
        log.info("异步同步订单操作");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("fields","total_commission_fee,total_commission_rate,tb_trade_parent_id,tk_status,tb_trade_id,trade_id,num_iid,item_title,item_num,price,pay_price,seller_nick,seller_shop_title,commission,commission_rate,unid,create_time,earning_time,tk3rd_pub_id,tk3rd_site_id,tk3rd_adzone_id,relation_id");
        json.put("start_time",sdf.format(date));
        json.put("span",1200);
        json.put("page_size",100);
        json.put("tk_status",1);
        json.put("order_query_type","create_time");
        json.put("session",session);
        String result = null;
        Map<String,String> map = new HashMap<>();
        map.put("Content-Type","application/json");
        try {
            Thread.sleep(i*3000);
            result = HttpUtil.post(KoussUrl,json.toJSONString(),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( result.indexOf("\"code\":106,\"msg\":\"Too fast\",\"sub_msg\":\"请求频率过快\"") > -1 ){
            try {
                Thread.sleep(i*1000);
                orderGet(session,date,i);
                return ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!StringUtils.isEmpty(result)){
            JSONObject resultjson = JSONObject.fromObject(JSONObject.fromObject(result).get("tbk_sc_order_get_response").toString());
            if( resultjson.get("results").toString().length() > 60 ){
                JSONArray jsonlist = JSONArray.fromObject(JSONObject.fromObject(resultjson.get("results").toString()).get("n_tbk_order").toString());
                for(Object obj : jsonlist){
                    JSONObject jsonObject = JSONObject.fromObject(obj.toString());
                    MmSysOrders mmSysOrders = new MmSysOrders();
                    mmSysOrders.setAdzoneId(jsonObject.get("adzone_id").toString());
                    mmSysOrders.setAdzoneName(jsonObject.get("adzone_name").toString());
                    mmSysOrders.setAlipayTotalPrice(jsonObject.get("alipay_total_price").toString());
                    mmSysOrders.setAuctionCategory(jsonObject.get("auction_category").toString());
                    mmSysOrders.setCommission(jsonObject.get("commission").toString());
                    mmSysOrders.setCommissionRate(jsonObject.get("commission_rate").toString());
                    try {
                        mmSysOrders.setEarningTime(sdf.parse(jsonObject.get("create_time").toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mmSysOrders.setIncomeRate(jsonObject.get("income_rate").toString());
                    mmSysOrders.setItemNum(Integer.parseInt(jsonObject.get("item_num").toString()));
                    mmSysOrders.setItemTitle(jsonObject.get("item_title").toString());
                    mmSysOrders.setNumIid(jsonObject.getInt("num_iid"));
                    mmSysOrders.setOrderType(jsonObject.get("order_type").toString());
                    mmSysOrders.setPayPrice(jsonObject.get("pay_price").toString());
                    mmSysOrders.setPrice(jsonObject.get("price").toString());
                    mmSysOrders.setPubSharePreFee(jsonObject.get("pub_share_pre_fee").toString());
                    mmSysOrders.setSellerNick(jsonObject.get("seller_nick").toString());
                    mmSysOrders.setSellerShopTitle(jsonObject.get("seller_shop_title").toString());
                    mmSysOrders.setSiteId(jsonObject.get("site_id").toString());
                    mmSysOrders.setSiteName(jsonObject.get("site_name").toString());
//                    mmSysOrders.setSubsidyFee(jsonObject.get("subsidy_fee").toString());
//                    mmSysOrders.setSubsidyRate(jsonObject.get("subsidy_rate").toString());
//                    mmSysOrders.setSubsidyType(jsonObject.get("subsidy_type").toString());
                    mmSysOrders.setTerminalType(jsonObject.get("terminal_type").toString());
                    mmSysOrders.setTkStatus(Integer.parseInt(jsonObject.get("tk_status").toString()));
//                    mmSysOrders.setTotalCommissionFee(jsonObject.get("total_commission_fee").toString());
//                    mmSysOrders.setTotalCommissionRate(jsonObject.get("total_commission_rate").toString());
                    mmSysOrders.setTradeId(jsonObject.getInt("trade_id"));
                    mmSysOrders.setTradeParentId(jsonObject.getInt("trade_parent_id"));
                    ordersService.insertByTradeId(mmSysOrders);
                }
            }

        }
    }
    @Async("myExecutor")
    public void orderCalculation(String session, Date date){
        log.info("异步同步订单操作");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("同步"+sdf.format(date)+"的订单");
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("fields","total_commission_fee,total_commission_rate,tb_trade_parent_id,tk_status,tb_trade_id,trade_id,num_iid,item_title,item_num,price,pay_price,seller_nick,seller_shop_title,commission,commission_rate,unid,create_time,earning_time,tk3rd_pub_id,tk3rd_site_id,tk3rd_adzone_id,relation_id");
        json.put("start_time",sdf.format(date));
        json.put("span",1200);
        json.put("page_size",100);
        json.put("tk_status",1);
        json.put("order_query_type","create_time");
        json.put("session",session);
        String result = null;
        Map<String,String> map = new HashMap<>();
        map.put("Content-Type","application/json");
        try {
            result = HttpUtil.post(KoussUrl,json.toJSONString(),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        if( result.indexOf("\"code\":106,\"msg\":\"Too fast\",\"sub_msg\":\"请求频率过快\"") > -1 ){
            try {
                Thread.sleep(3000);
                orderCalculation(session,date);
                return ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!StringUtils.isEmpty(result)){
            JSONObject resultjson = JSONObject.fromObject(JSONObject.fromObject(result).get("tbk_sc_order_get_response").toString());
            if( resultjson.get("results").toString().length() > 60 ){
                JSONArray jsonlist = JSONArray.fromObject(JSONObject.fromObject(resultjson.get("results").toString()).get("n_tbk_order").toString());
                for(Object obj : jsonlist){
                    JSONObject jsonObject = JSONObject.fromObject(obj.toString());
                    MmSysOrders mmSysOrders = new MmSysOrders();
                    mmSysOrders.setAdzoneId(jsonObject.get("adzone_id").toString());
                    mmSysOrders.setAdzoneName(jsonObject.get("adzone_name").toString());
                    mmSysOrders.setAlipayTotalPrice(jsonObject.get("alipay_total_price").toString());
                    mmSysOrders.setAuctionCategory(jsonObject.get("auction_category").toString());
                    mmSysOrders.setCommission(jsonObject.get("commission").toString());
                    mmSysOrders.setCommissionRate(jsonObject.get("commission_rate").toString());
                    try {
                        mmSysOrders.setEarningTime(sdf.parse(jsonObject.get("create_time").toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mmSysOrders.setIncomeRate(jsonObject.get("income_rate").toString());
                    mmSysOrders.setItemNum(Integer.parseInt(jsonObject.get("item_num").toString()));
                    mmSysOrders.setItemTitle(jsonObject.get("item_title").toString());
                    mmSysOrders.setNumIid(jsonObject.getInt("num_iid"));
                    mmSysOrders.setOrderType(jsonObject.get("order_type").toString());
                    mmSysOrders.setPayPrice(jsonObject.get("pay_price").toString());
                    mmSysOrders.setPrice(jsonObject.get("price").toString());
                    mmSysOrders.setPubSharePreFee(jsonObject.get("pub_share_pre_fee").toString());
                    mmSysOrders.setSellerNick(jsonObject.get("seller_nick").toString());
                    mmSysOrders.setSellerShopTitle(jsonObject.get("seller_shop_title").toString());
                    mmSysOrders.setSiteId(jsonObject.get("site_id").toString());
                    mmSysOrders.setSiteName(jsonObject.get("site_name").toString());
                    mmSysOrders.setTerminalType(jsonObject.get("terminal_type").toString());
                    mmSysOrders.setTkStatus(Integer.parseInt(jsonObject.get("tk_status").toString()));
                    mmSysOrders.setTradeId(jsonObject.getInt("trade_id"));
                    mmSysOrders.setTradeParentId(jsonObject.getInt("trade_parent_id"));
                    ordersService.insertByTradeId(mmSysOrders);
                }
            }

        }
    }

    @Async("myExecutor")
    public void sendMessQrcode(MmSysUser mmSysUser, MmSysGzh mmSysGzh, Boolean CreateQrcodeFlag, String QrcodeAppId, String QrcodeAppSecret){
        log.info("异步获取邀请二维码");
            CreateQrcode createQrcode = new CreateQrcode();
        MmSysQrcode mmSysQrcode1 = mmSysQrcodeMapper.selectByPrimaryKey(mmSysGzh.getGzhname());
        if( StringUtils.isEmpty(mmSysUser.getQrcodeTicket()) ) {
            String result = createQrcode.getQrcodeTitke(loginController.getAccess_token(mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret()).getString("access_token"),mmSysUser.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), CreateQrcodeFlag, QrcodeAppId, QrcodeAppSecret);
            mmSysUser.setQrcodeTicket(JSONObject.fromObject(result).getString("ticket"));
            userService.updateByPrimaryKeySelective(mmSysUser);
        }
        String fileName = null;
        try {
            fileName = createQrcode.getCodeFileName(mmSysUser.getQrcodeTicket(),mmSysQrcode1.getEws(),mmSysQrcode1.getEhs(),mmSysQrcode1.getBxs(),mmSysQrcode1.getBys(),mmSysQrcode1.getGzhname());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!StringUtils.isEmpty(fileName)){
            String result = null;
            try {
                result = createQrcode.HcUploadFile(loginController.getAccess_token(mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret()).getString("access_token"),fileName,"image", mmSysUser.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), CreateQrcodeFlag, QrcodeAppId, QrcodeAppSecret);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if( result.indexOf("media_id") > -1 ){
                String media_id = JSONObject.fromObject(result).getString("media_id");
                new File(fileName).delete();
                SendKfMessImage( mmSysUser.getOpenid(), mmSysGzh.getGzhname(),  mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), media_id);
            }
        }

        // return JSONObject.fromObject(result).getString("ticket");
    }
    @Async("myExecutor")
    public void updateMenu(MmSysGzh mmSysGzh, MmSysSetting mmSysSetting){
        log.info("异步更新菜单");
        try {
            HttpUtil.post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+loginController.getAccess_token(mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret()).getString("access_token"),mmSysSetting.getMenu(),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
