package com.pojo;

import java.util.Date;

public class MmSysGzhToken {
    /**
     * 
     */
    private String gzhname;

    /**
     * 
     */
    private String accesstoken;

    /**
     * 
     */
    private Date time;

    public String getGzhname() {
        return gzhname;
    }

    public void setGzhname(String gzhname) {
        this.gzhname = gzhname;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}