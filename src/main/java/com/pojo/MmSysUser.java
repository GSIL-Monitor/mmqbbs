package com.pojo;

import java.util.Date;

public class MmSysUser {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 
     */
    private String openid;

    /**
     * 
     */
    private String openid1;

    /**
     * 
     */
    private String openid2;

    /**
     * 
     */
    private String openid3;

    /**
     * 
     */
    private String pid;

    /**
     * 
     */
    private Date pidtime;

    /**
     * 
     */
    private String gzhname;

    /**
     * 邀请二维码ticket
     */
    private String qrcodeTicket;

    /**
     * 
     */
    private Double watingMoney;

    /**
     * 
     */
    private Double alreadyMoney;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenid1() {
        return openid1;
    }

    public void setOpenid1(String openid1) {
        this.openid1 = openid1;
    }

    public String getOpenid2() {
        return openid2;
    }

    public void setOpenid2(String openid2) {
        this.openid2 = openid2;
    }

    public String getOpenid3() {
        return openid3;
    }

    public void setOpenid3(String openid3) {
        this.openid3 = openid3;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getPidtime() {
        return pidtime;
    }

    public void setPidtime(Date pidtime) {
        this.pidtime = pidtime;
    }

    public String getGzhname() {
        return gzhname;
    }

    public void setGzhname(String gzhname) {
        this.gzhname = gzhname;
    }

    public String getQrcodeTicket() {
        return qrcodeTicket;
    }

    public void setQrcodeTicket(String qrcodeTicket) {
        this.qrcodeTicket = qrcodeTicket;
    }

    public Double getWatingMoney() {
        return watingMoney;
    }

    public void setWatingMoney(Double watingMoney) {
        this.watingMoney = watingMoney;
    }

    public Double getAlreadyMoney() {
        return alreadyMoney;
    }

    public void setAlreadyMoney(Double alreadyMoney) {
        this.alreadyMoney = alreadyMoney;
    }
}