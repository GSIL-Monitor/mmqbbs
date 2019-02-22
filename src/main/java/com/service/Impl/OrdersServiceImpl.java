package com.service.Impl;

import com.dao.*;
import com.pojo.*;
import com.service.OrdersService;
import com.task.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private MmSysOrdersMapper mmSysOrdersMapper;
    @Autowired
    private MmSysGzhMapper mmSysGzhMapper;
    @Autowired
    private MmSysUserMapper mmSysUserMapper;
    @Autowired
    private MmSysSettingMapper mmSysSettingMapper;
    @Autowired
    private MmSysUserOrdersMapper mmSysUserOrdersMapper;
    @Autowired
    @Lazy
    private AsyncTaskService asyncTaskService;

    @Override
    public Integer insertByTradeId(MmSysOrders mmSysOrders) {
        int i = 0;
        Boolean newOrders = false;
        if( mmSysOrdersMapper.selectCountByTradeId(mmSysOrders) > 0 ){
            MmSysOrders mmSysOrdersOld = mmSysOrdersMapper.selectByTradeId(mmSysOrders);
            if( mmSysOrdersOld.getTkStatus() == mmSysOrders.getTkStatus() ){
                return 0;
            }
            mmSysOrders.setId(mmSysOrdersOld.getId());
            i = mmSysOrdersMapper.updateByPrimaryKey(mmSysOrders);
        }else{
            newOrders = true;
            i = mmSysOrdersMapper.insert(mmSysOrders);
        }
        if( i > 0 ){
            mmSysOrders = mmSysOrdersMapper.selectByTradeId(mmSysOrders);
        }
        MmSysUser mmSysUser  = mmSysUserMapper.selectByPid(mmSysOrders.getAdzoneId());
        if( mmSysUser == null ){
            return 0;
        }
        mmSysUser.setPidtime(new Date());
        mmSysUserMapper.updateByPrimaryKey(mmSysUser);
        MmSysGzh mmSysGzh = mmSysGzhMapper.selectByPrimaryKey(mmSysUser.getGzhname());
        MmSysSetting mmSysSetting =  mmSysSettingMapper.selectByGzhName(mmSysGzh.getGzhname());
        MmSysUserOrders mmSysUserOrders = new MmSysUserOrders();
        NumberFormat ddf1= NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        String context = "";
        System.out.println("订单状态编码:"+mmSysOrders.getTkStatus().toString());
        switch (mmSysOrders.getTkStatus()){
            case 3:
                 context = "一一一一奖 励 成 功一一一一\n" +
                        mmSysOrders.getItemTitle()+"\n" +
                        "任务编号:" +
                        mmSysOrders.getTradeId().toString().replace("-","")+"\n" +
                        "任务消耗爱心:"+mmSysOrders.getAlipayTotalPrice()+"个\n" +
                        "奖励爱心:"+ddf1.format(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100)+"个\n" +
                        "\n" +
                        "当前任务完成,发放爱心到背包\n" +
                        "当前爱心数量:"+mmSysUser.getAlreadyMoney()+"个\n" +
                        "任务进行中爱心数量:"+mmSysUser.getWatingMoney() +"个\n" +
                        "\n" +
                         "奖励爱心累计满1个后，直接发：\"爱心兑换\"给"+mmSysGzh.getGzhnc()+"，24小时内"+mmSysGzh.getGzhnc()+"会发红包给您哦~";
                mmSysUser.setWatingMoney((mmSysUser.getWatingMoney()==null?0:mmSysUser.getWatingMoney())-Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
                mmSysUser.setAlreadyMoney(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100+(mmSysUser.getAlreadyMoney()==null?0:mmSysUser.getAlreadyMoney()));
                mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
                asyncTaskService.SendKfMessText(mmSysUser.getOpenid(),mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),context);
                break;
            case 12:
                 context = "一一一一接 受 任 务一一一一\n" +
                        mmSysOrders.getItemTitle()+"\n" +
                        "任务编号:" +
                        mmSysOrders.getTradeId().toString().replace("-","")+"\n" +
                        "任务消耗爱心:"+mmSysOrders.getAlipayTotalPrice()+"个\n" +
                        "预计奖励爱心:"+ddf1.format(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100)+"个\n" +
                        "\n" +
                        "当前任务完成,奖励爱心增加到背包\n" +
                         "当前爱心数量:"+mmSysUser.getAlreadyMoney()+"个\n" +
                         "任务进行中爱心数量:"+mmSysUser.getWatingMoney() +"个\n" +
                        "\n" +
                         "奖励爱心累计满1个后，直接发：\"爱心兑换\"给"+mmSysGzh.getGzhnc()+"，24小时内"+mmSysGzh.getGzhnc()+"会发红包给您哦~";
                mmSysUser.setWatingMoney((mmSysUser.getWatingMoney()==null?0:mmSysUser.getWatingMoney())+Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
                mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
                asyncTaskService.SendKfMessText(mmSysUser.getOpenid(),mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),context);
                break;
            case 13:
                 context = "一一一一任 务 失 败一一一一\n" +
                        mmSysOrders.getItemTitle()+"\n" +
                        "任务编号:" +
                        mmSysOrders.getTradeId().toString().replace("-","")+"\n" +
                        "任务消耗爱心:"+mmSysOrders.getAlipayTotalPrice()+"个\n" +
                        "得到爱心:"+ddf1.format(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100)+"个\n" +
                        "\n" +
                        "当前任务完成,奖励爱心增加到背包\n" +
                         "当前爱心数量:"+mmSysUser.getAlreadyMoney()+"个\n" +
                         "任务进行中爱心数量:"+mmSysUser.getWatingMoney() +"个\n" +
                        "\n" +
                         "奖励爱心累计满1个后，直接发：\"爱心兑换\"给"+mmSysGzh.getGzhnc()+"，24小时内"+mmSysGzh.getGzhnc()+"会发红包给您哦~";
                 if(!newOrders){
                    mmSysUser.setWatingMoney((mmSysUser.getWatingMoney()==null?0:mmSysUser.getWatingMoney())-Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
                    mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
                 }
                asyncTaskService.SendKfMessText(mmSysUser.getOpenid(),mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),context);
                break;
            case 14:
                 context = "一一一一任 务 完 成一一一一\n" +
                        mmSysOrders.getItemTitle()+"\n" +
                        "任务编号:" +
                        mmSysOrders.getTradeId().toString().replace("-","")+"\n" +
                        "任务消耗爱心:"+mmSysOrders.getAlipayTotalPrice()+"个\n" +
                        "预计奖励爱心:"+ddf1.format(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100)+"个\n" +
                        "\n" +
                        "当前任务完成,等待爱心发放到背包\n" +
                         "当前爱心数量:"+mmSysUser.getAlreadyMoney()+"个\n" +
                         "任务进行中爱心数量:"+mmSysUser.getWatingMoney() +"个\n" +
                        "\n" +
                        "奖励爱心累计满1个后，直接发：\"爱心兑换\"给"+mmSysGzh.getGzhnc()+"，24小时内"+mmSysGzh.getGzhnc()+"会发红包给您哦~";
                 mmSysUser.setWatingMoney((mmSysUser.getWatingMoney()==null?0:mmSysUser.getWatingMoney())+Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
                asyncTaskService.SendKfMessText(mmSysUser.getOpenid(),mmSysGzh.getGzhname(),mmSysGzh.getWxappid(),mmSysGzh.getWxappsecret(),context);
                break;
            default:
                return 0;
        }

        DecimalFormat df1 = new DecimalFormat("0.00%");    //##.00%   百分比格式，后面不足2位的用0补齐
        switch (mmSysOrders.getTkStatus()) {
            case 3:
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid1())) {
                        context = "您班级的同学完成了购物任务，并成功拿到了奖励，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel2().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        open1.setAlreadyMoney(Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 + (open1.getAlreadyMoney() == null ? 0 : open1.getAlreadyMoney()));
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid2())) {
                        context = "您班级的同学完成了购物任务，并成功拿到了奖励，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel3().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        open1.setAlreadyMoney(Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 + (open1.getAlreadyMoney() == null ? 0 : open1.getAlreadyMoney()));
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid3())) {
                        context = "您班级的同学完成了购物任务，并成功拿到了奖励，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel4().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        open1.setAlreadyMoney(Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 + (open1.getAlreadyMoney() == null ? 0 : open1.getAlreadyMoney()));
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                break;
            case 12:

                    if (!StringUtils.isEmpty(mmSysUser.getOpenid1())) {
                        context = "您班级的同学接受了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel2().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) + Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid2())) {
                        context = "您班级的同学接受了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel3().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) + Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid3())) {
                        context = "您班级的同学接受了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel4().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"的任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) + Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                break;
            case 13:
                if(!newOrders) {

                    if (!StringUtils.isEmpty(mmSysUser.getOpenid1())) {
                        context = "您班级的同学任务失败了，扣除您的任务中爱心为同学任务奖金的"+df1.format(Double.parseDouble(mmSysSetting.getLevel2().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"，约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context + Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 );
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid2())) {
                        context = "您班级的同学任务失败了，扣除您的任务中爱心为同学任务奖金的"+df1.format(Double.parseDouble(mmSysSetting.getLevel3().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"，约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context + Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 );
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid3())) {
                        context = "您班级的同学任务失败了，扣除您的任务中爱心为同学任务奖金的"+df1.format(Double.parseDouble(mmSysSetting.getLevel4().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"，约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context + Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100 );
                    }
                }
                break;
            case 14:

                    if (!StringUtils.isEmpty(mmSysUser.getOpenid1())) {
                        context = "您班级的同学完成了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel2().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel2().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid2())) {
                        context = "您班级的同学完成了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel3().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel3().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                    if (!StringUtils.isEmpty(mmSysUser.getOpenid3())) {
                        context = "您班级的同学完成了购物任务，恭喜赚到了他"+df1.format(Double.parseDouble(mmSysSetting.getLevel4().toString())/Double.parseDouble(mmSysSetting.getLevel1().toString()))
                                +"任务中爱心奖励约：";
                        MmSysUser open1 = mmSysUserMapper.selectByOpenid(mmSysUser.getOpenid1());
                        open1.setWatingMoney((open1.getWatingMoney() == null ? 0 : open1.getWatingMoney()) - Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100);
                        mmSysUserMapper.updateByPrimaryKey(open1);
                        asyncTaskService.SendKfMessText(open1.getOpenid(), mmSysGzh.getGzhname(), mmSysGzh.getWxappid(), mmSysGzh.getWxappsecret(), context+Double.parseDouble(mmSysSetting.getLevel4().toString()) * Double.parseDouble(mmSysOrders.getPubSharePreFee()) / 100+"可以体现哦！");
                    }
                break;
        }
        mmSysUserOrders.setUid(mmSysUser.getId());
        mmSysUserOrders.setOid(mmSysOrders.getId());
        mmSysUserOrders.setOrderMoney(Double.parseDouble(mmSysOrders.getAlipayTotalPrice()));
        mmSysUserOrders.setRebateMoney(Double.parseDouble(mmSysSetting.getLevel1().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
        mmSysUserOrders.setGznname(mmSysGzh.getGzhname());
        mmSysUserOrders.setProfits(Double.parseDouble(mmSysOrders.getPubSharePreFee())-mmSysUserOrders.getRebateMoney());
        if(!StringUtils.isEmpty(mmSysUser.getOpenid1())){
            mmSysUserOrders.setLevel1(Double.parseDouble(mmSysSetting.getLevel2().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
            mmSysUserOrders.setOpenid1(mmSysUser.getOpenid1());
            mmSysUserOrders.setProfits(mmSysUserOrders.getProfits()-mmSysUserOrders.getLevel1());
        }
        if(!StringUtils.isEmpty(mmSysUser.getOpenid2())){
            mmSysUserOrders.setLevel2(Double.parseDouble(mmSysSetting.getLevel3().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
            mmSysUserOrders.setOpenid2(mmSysUser.getOpenid2());
            mmSysUserOrders.setProfits(mmSysUserOrders.getProfits()-mmSysUserOrders.getLevel2());
        }
        if(!StringUtils.isEmpty(mmSysUser.getOpenid3())){
            mmSysUserOrders.setLevel3(Double.parseDouble(mmSysSetting.getLevel4().toString())*Double.parseDouble(mmSysOrders.getPubSharePreFee())/100);
            mmSysUserOrders.setOpenid3(mmSysUser.getOpenid3());
            mmSysUserOrders.setProfits(mmSysUserOrders.getProfits()-mmSysUserOrders.getLevel3());
        }
        if(mmSysOrders.getTkStatus() == 3){
            mmSysUserOrders.setFlag(3);
            if (mmSysUserOrdersMapper.updateByPrimaryKey(mmSysUserOrders)< 1 ){
                mmSysUserOrdersMapper.insert(mmSysUserOrders);
            }
        }
        if( mmSysOrders.getTkStatus() == 12 ||  mmSysOrders.getTkStatus() == 14 ){
            mmSysUserOrders.setFlag(0);
            if ( mmSysUserOrdersMapper.updateByPrimaryKey(mmSysUserOrders) < 1 ){
                mmSysUserOrdersMapper.insert(mmSysUserOrders);
            }
        }
        if( mmSysOrders.getTkStatus() == 13 ){
            mmSysUserOrders.setFlag(2);
            if ( mmSysUserOrdersMapper.updateByPrimaryKey(mmSysUserOrders)< 1 ){
                mmSysUserOrdersMapper.insert(mmSysUserOrders);
            }
        }
        return 1;
    }
}
