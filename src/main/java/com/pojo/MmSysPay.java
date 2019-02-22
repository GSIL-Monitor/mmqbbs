package com.pojo;

public class MmSysPay {
    /**
     * 
     */
    private Integer id;

    /**
     * 商户号
     */
    private Integer mchId;

    /**
     * 微信appid
     */
    private String appid;

    /**
     * 商户名称
     */
    private String sendName;

    /**
     * 祝福语
     */
    private String wishing;

    /**
     * 商户支付API秘钥
     */
    private String zfkey;

    /**
     * 
     */
    private String clientIp;

    /**
     * 
     */
    private String actName;

    /**
     * 
     */
    private String remark;

    /**
     * 
     */
    private String gzhname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMchId() {
        return mchId;
    }

    public void setMchId(Integer mchId) {
        this.mchId = mchId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getZfkey() {
        return zfkey;
    }

    public void setZfkey(String zfkey) {
        this.zfkey = zfkey;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGzhname() {
        return gzhname;
    }

    public void setGzhname(String gzhname) {
        this.gzhname = gzhname;
    }
}