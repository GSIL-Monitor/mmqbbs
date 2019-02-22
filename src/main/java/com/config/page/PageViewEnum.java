package com.config.page;

/**
 * Description: 页面跳转注册
 * author: yu.hb
 * Date: 2018-12-13
 */
public enum PageViewEnum {
    index("/index","index")
    ;

    public String url; // url地址

    public String viewName; // 页面名称


    PageViewEnum(String url, String viewName) {
        this.url = url;
        this.viewName = viewName;
    }

}
