package com.service.Impl;/**
 * 描述：
 * Created by Liuyg on 2018-12-31 15:12
 */

import com.alibaba.fastjson.JSONObject;
import com.dao.MmSysPayMapper;
import com.dao.MmSysPayRecordMapper;
import com.dao.MmSysUserMapper;
import com.pay.MD5;
import com.pojo.MmSysGzh;
import com.pojo.MmSysPay;
import com.pojo.MmSysPayRecord;
import com.pojo.MmSysUser;
import com.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-31 15:12
 **/
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private MmSysPayMapper mmSysPayMapper;
    @Autowired
    private MmSysPayRecordMapper mmSysPayRecordMapper;
    @Autowired
    private MmSysUserMapper mmSysUserMapper;
    @Override
    public void sendRedPack(MmSysGzh mmSysGzh, MmSysUser mmSysUser) {
        MmSysPay mmSysPay = mmSysPayMapper.selectByGzhName(mmSysGzh.getGzhname());
        MmSysPayRecord mmSysPayRecord = new MmSysPayRecord();
        Double AlreadyMoney = mmSysUser.getAlreadyMoney()*100;
        mmSysUser.setAlreadyMoney(0D);
        mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
        MD5 paymd = new MD5();//发送红包类
        Boolean flag = false;
        String result = "";
        try {
            result = paymd.sendRedPack(mmSysPay.getMchId().toString(),mmSysPay.getAppid(),mmSysUser.getOpenid(),mmSysPay.getSendName(),mmSysPay.getWishing(),
                    mmSysPay.getZfkey(),mmSysPay.getClientIp(),mmSysPay.getActName(),mmSysPay.getRemark(),mmSysGzh.getGzhname(),AlreadyMoney.toString().substring(0,AlreadyMoney.toString().lastIndexOf(".")));
        } catch (Exception e) {
            flag = true;
        }
        JSONObject json = null;
        try {
            json =  JSONObject.parseObject(result);
        }catch (Exception e){
            flag = true;
        }
        if( json != null && "SUCCESS".equals(json.getString("return_code")) ){
            if("SUCCESS".equals(json.getString("result_code"))){
                mmSysPayRecord.setReOpenid(json.getString("re_openid"));
                mmSysPayRecord.setTotalAmount(json.getInteger("total_amount"));
                mmSysPayRecord.setErrCode(json.getString("err_code"));
                mmSysPayRecord.setReturnMsg(json.getString("return_msg"));
                mmSysPayRecord.setResultCode(json.getString("result_code"));
                mmSysPayRecord.setErrCodeDes(json.getString("err_code_des"));
                mmSysPayRecord.setMchId(json.getString("mch_id"));
                mmSysPayRecord.setSendListid(json.getString("send_listid"));
                mmSysPayRecord.setReturnCode(json.getString("return_code"));
                mmSysPayRecord.setWxappid(json.getString("wxappid"));
                mmSysPayRecord.setMchBillno(json.getString("mch_billno"));
                mmSysPayRecordMapper.insert(mmSysPayRecord);
            }else{
                if(!"SYSTEMERROR".equals(json.getString("err_code"))){
                    flag = true;
                }
            }
        }else{
            flag = true;
        }
        if(flag){
            mmSysUser.setAlreadyMoney(AlreadyMoney/100);
            mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
        }

    }
}
