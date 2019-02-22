package com.controller;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 赵贺飞
 * @Date: 2018/3/28 16:05
 * 调用微信公众平台主动发消息接口
 */
public class wechat {

    private static Map<String,Map<String,Object>> accessTokenMap = new HashMap<>();
    public static void main(String[] args) throws IOException {

       /* String appId = "wx613166a2574d5868";
        String appSecret = "adc992cabcd1b452e8c44be347e71b99";
        String token = getAccess_token("gzhname",appId, appSecret).getString("access_token");
        System.err.println("Tocken：" + token);*/

        //发送消息
        /*JSONObject text = new JSONObject();
        text.element("content", "内容");
        JSONObject json = new JSONObject();
        json.element("touser", "oNLtTw6H2Rx5Qg-SWaEjsbKfPkGY");
        json.element("text", text);
        json.element("msgtype", "text");*/
        // customSend(json, token);

        //发送图片
        /*JSONObject media_id = new JSONObject();
        String filePath = "E:\\SougouImages\\SogouWP\\Net\\WallPaper\\405376.jpg";
        media_id.element("media_id", upload(filePath, token, "image"));
        JSONObject json1 = new JSONObject();
        json1.element("touser", "oNLtTw6H2Rx5Qg-SWaEjsbKfPkGY");
        json1.element("image", media_id);
        json1.element("msgtype", "image");
        customSend(json1, token);*/
    }

    /**
     * 获取code
     */
    //https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=redirect_uri&response_type=code&scope=snsapi_base&state=1&connect_redirect=1#wechat_redirect

    /**
     * 获取code后，根据code获取openid
     */
    //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=secret&code=$code&grant_type=authorization_code


    /**
     * 发送图片
     * @param filePath
     * @param accessToken
     * @param type
     * @return
     * @throws IOException
     */
    public static String upload(String filePath, String accessToken, String type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        //String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+ accessToken +"&type="+ type;
        URL urlobj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlobj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        //设置头信息
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNFARY = "----------" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNFARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNFARY);
        sb.append("\r\n");
        sb.append("Content-Disposition:from-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/actet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes(StandardCharsets.UTF_8);
        //获得输出流
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件以流的方式 推入到url
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();
        //结尾部分
        byte[] foot = ("\r\n--" + BOUNFARY + "--\r\n").getBytes(StandardCharsets.UTF_8);
        out.write(foot);
        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        if (result == null) {
            result = buffer.toString();
        }
        if (result != null) {
            reader.close();
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        System.out.println(jsonObject);
        String typeName = "media_id";
        if (!"image".equals(type) && !"voice".equals(type) && !"video".equals(type)) {
            typeName = type + "_media_id";
        }
        String mediaid = jsonObject.getString(typeName);
        return mediaid;
    }


    /**
     * post请求,发送消息
     */
    public static void customSend(JSONObject json, String accessToken) {

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(json.toString());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("向客服发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 获取token
     *
     * @return
     */
    public static JSONObject getAccess_tokenasd(String gzhname,String appId, String appSecret) {
        Map<String,Object> map = null;
        Long time = null;
        String accessToken = null;
        if(accessTokenMap.containsKey(gzhname)){
            map = accessTokenMap.get(gzhname);
            if(map.containsKey("time")){
                time = (Long)map.get("time");
                if( (time - System.currentTimeMillis()) < (1000*60*100) ) {
                    return JSONObject.fromObject(map.get("accessToken").toString());
                }else{
                    accessTokenMap.remove(gzhname);
                }
            }else{
                accessTokenMap.remove(gzhname);
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
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            accessToken = new String(jsonBytes, StandardCharsets.UTF_8);
            time=System.currentTimeMillis();
            map = new HashMap<>();
            map.put("time",time);
            map.put("accessToken",accessToken);
            accessTokenMap.put(gzhname,map);
            jsonObj = JSONObject.fromObject(accessToken);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}