package com.pojo;

public class MmSysQrcode {
    /**
     * 公众号名称
     */
    private String gzhname;

    /**
     * 二维码宽度
     */
    private Integer ews;

    /**
     * 二维码高度
     */
    private Integer ehs;

    /**
     * 背景图定位x
     */
    private Integer bxs;

    /**
     * 背景图定位y
     */
    private Integer bys;

    public String getGzhname() {
        return gzhname;
    }

    public void setGzhname(String gzhname) {
        this.gzhname = gzhname;
    }

    public Integer getEws() {
        return ews;
    }

    public void setEws(Integer ews) {
        this.ews = ews;
    }

    public Integer getEhs() {
        return ehs;
    }

    public void setEhs(Integer ehs) {
        this.ehs = ehs;
    }

    public Integer getBxs() {
        return bxs;
    }

    public void setBxs(Integer bxs) {
        this.bxs = bxs;
    }

    public Integer getBys() {
        return bys;
    }

    public void setBys(Integer bys) {
        this.bys = bys;
    }
}