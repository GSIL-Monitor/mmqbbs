package com.pojo;

public class MmSysUserOrders {
    /**
     * 
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 订单id
     */
    private Integer oid;

    /**
     * 订单金额
     */
    private Double orderMoney;

    /**
     * 返利金额
     */
    private Double rebateMoney;

    /**
     * 返利状态：0待返，1以返，2订单失效
     */
    private Integer flag;

    /**
     * 公众号名字
     */
    private String gznname;

    /**
     * 利润
     */
    private Double profits;

    /**
     * 一级返利
     */
    private Double level1;

    /**
     * 二级返利
     */
    private Double level2;

    /**
     * 三级返利
     */
    private Double level3;

    /**
     * 一级用户openId
     */
    private String openid1;

    /**
     * 二级用户openId
     */
    private String openid2;

    /**
     * 三级用户openId
     */
    private String openid3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Double orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Double getRebateMoney() {
        return rebateMoney;
    }

    public void setRebateMoney(Double rebateMoney) {
        this.rebateMoney = rebateMoney;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getGznname() {
        return gznname;
    }

    public void setGznname(String gznname) {
        this.gznname = gznname;
    }

    public Double getProfits() {
        return profits;
    }

    public void setProfits(Double profits) {
        this.profits = profits;
    }

    public Double getLevel1() {
        return level1;
    }

    public void setLevel1(Double level1) {
        this.level1 = level1;
    }

    public Double getLevel2() {
        return level2;
    }

    public void setLevel2(Double level2) {
        this.level2 = level2;
    }

    public Double getLevel3() {
        return level3;
    }

    public void setLevel3(Double level3) {
        this.level3 = level3;
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
}