package com.service.Impl;/**
 * 描述：
 * Created by Liuyg on 2018-12-27 18:58
 */

import com.dao.MmSysPidMapper;
import com.dao.MmSysUserMapper;
import com.pojo.MmSysPid;
import com.pojo.MmSysUser;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-27 18:58
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MmSysUserMapper mmSysUserMapper;
    @Autowired
    private MmSysPidMapper mmSysPidMapper;

    @Override
    public MmSysUser selectByOpenid(String openId) {
        return mmSysUserMapper.selectByOpenid(openId);
    }

    @Override
    public Integer insert(MmSysUser mmSysUser) {
        Date date = new Date();
        String pid = getPid(mmSysUser.getGzhname(),date);
        mmSysUser.setPid(pid);
        mmSysUser.setPidtime(date);
        return mmSysUserMapper.insert(mmSysUser);
    }

    @Override
    public Integer upPid(MmSysUser mmSysUser) {
        Date date = new Date();
        String pid = getPid(mmSysUser.getGzhname(),date);
        mmSysUser.setPid(pid);
        mmSysUser.setPidtime(date);
        return mmSysUserMapper.updateByPrimaryKey(mmSysUser);
    }

    @Override
    public Integer selectCountByOpenid1(String openId) {
        return mmSysUserMapper.selectCountByOpenid1(openId);
    }

    @Override
    public Integer selectCountByOpenid2(String openId) {
        return mmSysUserMapper.selectCountByOpenid2(openId);
    }

    @Override
    public Integer selectCountByOpenid3(String openId) {
        return mmSysUserMapper.selectCountByOpenid3(openId);
    }

    @Override
    public Integer updateByPrimaryKeySelective(MmSysUser mmSysUser) {
        return mmSysUserMapper.updateByPrimaryKeySelective(mmSysUser);
    }


    private String getPid(String gzhname,Date date){
        MmSysPid mmSysPid = mmSysPidMapper.selectPidByGzhName(gzhname);
        mmSysPid.setPidtime(date);
        mmSysPidMapper.updateByPrimaryKey(mmSysPid);
        return mmSysPid.getPid();
    }
}
