package com.pojo;

import java.util.Date;

public class MmSysOrders {
    /**
     * 
     */
    private Integer id;

    /**
     * 淘宝父订单号
     */
    private Integer tradeParentId;

    /**
     * 淘宝订单号
     */
    private Integer tradeId;

    /**
     * 商品ID
     */
    private Integer numIid;

    /**
     * 商品标题
     */
    private String itemTitle;

    /**
     * 商品数量
     */
    private Integer itemNum;

    /**
     * 单价
     */
    private String price;

    /**
     * 实际支付金额
     */
    private String payPrice;

    /**
     * 卖家昵称
     */
    private String sellerNick;

    /**
     * 卖家店铺名称
     */
    private String sellerShopTitle;

    /**
     * 推广者获得的收入金额，对应联盟后台报表“预估收入”
     */
    private String commission;

    /**
     * 推广者获得的分成比率，对应联盟后台报表“分成比率”
     */
    private String commissionRate;

    /**
     * 淘客订单创建时间
     */
    private Date createTime;

    /**
     * 淘客订单结算时间
     */
    private Date earningTime;

    /**
     * 淘客订单状态，3：订单结算，12：订单付款， 13：订单失效，14：订单成功
     */
    private Integer tkStatus;

    /**
     * 第三方服务来源，没有第三方服务，取值为“--”
     */
    private String tk3rdType;

    /**
     * 第三方推广者ID
     */
    private Integer tk3rdPubId;

    /**
     * 订单类型，如天猫，淘宝
     */
    private String orderType;

    /**
     * 收入比率，卖家设置佣金比率+平台补贴比率
     */
    private String incomeRate;

    /**
     * 效果预估，付款金额*(佣金比率+补贴比率)*分成比率
     */
    private String pubSharePreFee;

    /**
     * 补贴比率
     */
    private String subsidyRate;

    /**
     * 补贴类型，天猫:1，聚划算:2，航旅:3，阿里云:4
     */
    private String subsidyType;

    /**
     * 成交平台，PC:1，无线:2
     */
    private String terminalType;

    /**
     * 类目名称
     */
    private String auctionCategory;

    /**
     * 来源媒体ID
     */
    private String siteId;

    /**
     * 
     */
    private String siteName;

    /**
     * 
     */
    private String adzoneId;

    /**
     * 广告位名称
     */
    private String adzoneName;

    /**
     * 付款金额
     */
    private String alipayTotalPrice;

    /**
     * 佣金比率
     */
    private String totalCommissionRate;

    /**
     * 佣金金额
     */
    private String totalCommissionFee;

    /**
     * 补贴金额
     */
    private String subsidyFee;

    /**
     * 渠道关系ID
     */
    private Integer relationId;

    /**
     * 会员运营id
     */
    private Integer specialId;

    /**
     * 跟踪时间
     */
    private Date clickTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTradeParentId() {
        return tradeParentId;
    }

    public void setTradeParentId(Integer tradeParentId) {
        this.tradeParentId = tradeParentId;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getNumIid() {
        return numIid;
    }

    public void setNumIid(Integer numIid) {
        this.numIid = numIid;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public String getSellerNick() {
        return sellerNick;
    }

    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    public String getSellerShopTitle() {
        return sellerShopTitle;
    }

    public void setSellerShopTitle(String sellerShopTitle) {
        this.sellerShopTitle = sellerShopTitle;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(String commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEarningTime() {
        return earningTime;
    }

    public void setEarningTime(Date earningTime) {
        this.earningTime = earningTime;
    }

    public Integer getTkStatus() {
        return tkStatus;
    }

    public void setTkStatus(Integer tkStatus) {
        this.tkStatus = tkStatus;
    }

    public String getTk3rdType() {
        return tk3rdType;
    }

    public void setTk3rdType(String tk3rdType) {
        this.tk3rdType = tk3rdType;
    }

    public Integer getTk3rdPubId() {
        return tk3rdPubId;
    }

    public void setTk3rdPubId(Integer tk3rdPubId) {
        this.tk3rdPubId = tk3rdPubId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getIncomeRate() {
        return incomeRate;
    }

    public void setIncomeRate(String incomeRate) {
        this.incomeRate = incomeRate;
    }

    public String getPubSharePreFee() {
        return pubSharePreFee;
    }

    public void setPubSharePreFee(String pubSharePreFee) {
        this.pubSharePreFee = pubSharePreFee;
    }

    public String getSubsidyRate() {
        return subsidyRate;
    }

    public void setSubsidyRate(String subsidyRate) {
        this.subsidyRate = subsidyRate;
    }

    public String getSubsidyType() {
        return subsidyType;
    }

    public void setSubsidyType(String subsidyType) {
        this.subsidyType = subsidyType;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getAuctionCategory() {
        return auctionCategory;
    }

    public void setAuctionCategory(String auctionCategory) {
        this.auctionCategory = auctionCategory;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAdzoneId() {
        return adzoneId;
    }

    public void setAdzoneId(String adzoneId) {
        this.adzoneId = adzoneId;
    }

    public String getAdzoneName() {
        return adzoneName;
    }

    public void setAdzoneName(String adzoneName) {
        this.adzoneName = adzoneName;
    }

    public String getAlipayTotalPrice() {
        return alipayTotalPrice;
    }

    public void setAlipayTotalPrice(String alipayTotalPrice) {
        this.alipayTotalPrice = alipayTotalPrice;
    }

    public String getTotalCommissionRate() {
        return totalCommissionRate;
    }

    public void setTotalCommissionRate(String totalCommissionRate) {
        this.totalCommissionRate = totalCommissionRate;
    }

    public String getTotalCommissionFee() {
        return totalCommissionFee;
    }

    public void setTotalCommissionFee(String totalCommissionFee) {
        this.totalCommissionFee = totalCommissionFee;
    }

    public String getSubsidyFee() {
        return subsidyFee;
    }

    public void setSubsidyFee(String subsidyFee) {
        this.subsidyFee = subsidyFee;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Integer getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Integer specialId) {
        this.specialId = specialId;
    }

    public Date getClickTime() {
        return clickTime;
    }

    public void setClickTime(Date clickTime) {
        this.clickTime = clickTime;
    }
}