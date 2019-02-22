package com.controller;

import java.text.DecimalFormat;

public class test {
    public static void main(String[] args) {
       /* String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
        String json = "{\"expire_seconds\": 123, \"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
        String token = "17_0xBzU9pknFnW5Y5J5GPum7weHuYish2hElefxqGv-ScyBR6c6jPkM94xTIKzTp6fnWCw98AhS5fW7XjrwkUXCOVr_fHSLqjwzUkTKh0BVznvBtaWd9G4UCDSNesZEJaAHAQVA";
        String result = null;
        // String titke = "gQH-8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyM2QxSUJmekNlVGsxMDAwMGcwN3MAAgSKUChcAwQAAAAA";
        // Map<String,String> map = new HashMap<>();
        // map.put("Accept-Ranges","bytes");
        // map.put("Cache-control","max-age=604800");
        // map.put("Connection","keep-alive");
        // map.put("Content-Length","");
        // map.put("Content-Type","image/jpg");
        // map.put("Date","Wed, 16 Oct 2013 06:37:10 GMT");
        // map.put("Expires","Wed, 23 Oct 2013 14:37:10 +0800");
        // map.put("Server","nginx/1.4.1");
        // BufferedImage image = null;//二维码
        try {
            // result = HttpUtil.get("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+titke);
            result = HttpUtil.post(url+token,json,null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       /* String token = "17_0xBzU9pknFnW5Y5J5GPum7weHuYish2hElefxqGv-ScyBR6c6jPkM94xTIKzTp6fnWCw98AhS5fW7XjrwkUXCOVr_fHSLqjwzUkTKh0BVznvBtaWd9G4UCDSNesZEJaAHAQVA";
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+token;
        String json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
        String result = null;
        try {
            result = HttpUtil.post(url+token,json,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(result);*/
       // System.out.println(DigestUtils.md5Hex("ticket=gQH-8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyM2QxSUJmekNlVGsxMDAwMGcwN3MAAgSKUChcAwQAAAAA&gzhName=mmtest" + "mmqbb"));


          /*  Date date = new Date();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Date date2 = new Date();
            System.out.println(date.getTime()-date2.getTime());*/
          double d = Double.parseDouble("20")/Double.parseDouble("55");
        String result="";
        DecimalFormat df1 = new DecimalFormat("0.00%");    //##.00%   百分比格式，后面不足2位的用0补齐
        //result=nf.format(tempresult);
        result= df1.format(d);
        System.out.println(result);
    }

}