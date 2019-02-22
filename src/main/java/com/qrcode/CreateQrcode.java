package com.qrcode;/**
 * 描述：
 * Created by Liuyg on 2018-12-30 16:30
 */

import com.alibaba.druid.util.StringUtils;
import com.pojo.MmSysGzhToken;
import com.service.GzhService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.util.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-30 16:30
 **/

@Controller
public class CreateQrcode {
    @Autowired
    @Lazy
    private GzhService gzhService;

    // private static Map<String,Map<String,Object>> QrcodeaccessTokenMap = new HashMap<>();
    /**
    * @Description:  生成微信带参数二维码
    * @Param: [openid, gzhName, appId, appSecret, CreateQrcodeFlag, QrcodeAppId, QrcodeAppSecret]
    * @return: java.lang.String
    * @Author: Liuyg
    * @Date: 2018/12/30
    */
    public String getQrcodeTitke(String Access_token,String openid,String gzhName,String appId,String appSecret,Boolean CreateQrcodeFlag,String QrcodeAppId,String QrcodeAppSecret){
        // String Access_token = "";
        // if(CreateQrcodeFlag){
        //     // Access_token = getAccess_tokens(gzhName,QrcodeAppId, QrcodeAppSecret).getString("access_token");
        // }else{
        //     Access_token = getAccess_token(gzhName,appId, appSecret).getString("access_token");
        // }
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
        String json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+openid+"\"}}}";
        String result = null;
        try {
             result = HttpUtil.post(url+Access_token,json,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /*
     * 获取二维码图片和背景图合成一张
     */
    public String getCodeFileName(String titke,Integer ew,Integer eh,Integer bx,Integer by,String gzhName) throws Exception {
        URL url = new URL("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+titke);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(20 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();

        JPEGImageDecoder decoderFile = JPEGCodec.createJPEGDecoder(inStream);
        BufferedImage image = decoderFile.decodeAsBufferedImage();
        BufferedImage bg= ImageIO.read(new File(ResourceUtils.getFile("classpath:static/qrcodeTemplate/")+"/"+gzhName+".jpg"));//获取背景图片
        Graphics2D g=bg.createGraphics();
        g.drawImage(image,bx,by,ew,eh,null);
        g.dispose();
        bg.flush();
        image.flush();
        String fileName = ResourceUtils.getFile("classpath:static/qrcodeTemplate/")+"/"+UUID.randomUUID().toString().replace("-","")+".jpg";
        ImageIO.write(bg,"jpg", new File(fileName));
        return fileName;
    }

    /**微信上传素材
     * @param
     * @param filePath
     * @param type
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String HcUploadFile( String Access_token,String filePath,String type,String openid,String gzhName,String appId,String appSecret,Boolean CreateQrcodeFlag,String QrcodeAppId,String QrcodeAppSecret ) throws ClientProtocolException, IOException {
        // String Access_token = "";
        // if(CreateQrcodeFlag){
        //     // Access_token = getAccess_tokens(gzhName,QrcodeAppId, QrcodeAppSecret).getString("access_token");
        // }else{
        //     Access_token = getAccess_token(gzhName,appId, appSecret).getString("access_token");
        // }
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+Access_token+"&type="+type;
        HttpPost post = new HttpPost(url);
        File file = new File(filePath);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpEntity entity = null;
        HttpResponse response = null;
        String BoundaryStr = "------------7da2e536604c8";
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "multipart/form-data;boundary=" + BoundaryStr);
        post.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setBoundary(BoundaryStr).setCharset(Charset.forName("utf-8")).setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        meb.addBinaryBody("media", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        entity = meb.build();
        post.setEntity(entity);
        response = httpclient.execute(post);
        entity = response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");
        EntityUtils.consume(entity);// 关闭流
        return result;
    }

    /**
     * 获取token
     *
     * @return
     */
   /* public static JSONObject getAccess_tokens(String gzhname,String appId, String appSecret) {
        Map<String,Object> map = null;
        Long time = null;
        String accessToken = null;
        if(QrcodeaccessTokenMap.containsKey(gzhname)){
            map = QrcodeaccessTokenMap.get(gzhname);
            if(map.containsKey("time")){
                time = (Long)map.get("time");
                if( (time - System.currentTimeMillis()) < (1000*60*60) ) {
                    return JSONObject.fromObject(map.get("accessToken").toString());
                }else{
                    QrcodeaccessTokenMap.remove(gzhname);
                }
            }else{
                QrcodeaccessTokenMap.remove(gzhname);
            }
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        JSONObject jsonObj = null;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30200");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30010"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            accessToken = new String(jsonBytes, "UTF-8");
            time=System.currentTimeMillis();
            map = new HashMap<>();
            map.put("time",time);
            map.put("accessToken",accessToken);
            QrcodeaccessTokenMap.put(gzhname,map);
            jsonObj = JSONObject.fromObject(accessToken);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }*/

    /**
     * 获取token
     *
     * @return
     */
    public JSONObject getAccess_token(String gzhname, String appId, String appSecret) {
        MmSysGzhToken mmSysGzhToken = gzhService.selectGzhToken(gzhname);
        Date date = new Date();
        if( mmSysGzhToken != null && !StringUtils.isEmpty(mmSysGzhToken.getAccesstoken()) ){
            if( ( mmSysGzhToken.getTime().getTime() - date.getTime() ) < (1000*60*100) ) {
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
