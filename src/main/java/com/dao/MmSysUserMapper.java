package com.dao;

import com.pojo.MmSysUser;
import org.apache.ibatis.annotations.Param;

public interface MmSysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysUser record);

    int insertSelective(MmSysUser record);

    MmSysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysUser record);

    int updateByPrimaryKey(MmSysUser record);

    MmSysUser selectByOpenid(@Param("openId") String openId);

    MmSysUser selectByPid(@Param("pid") String pid);

    Integer selectCountByOpenid1(@Param("openId") String openId);

    Integer selectCountByOpenid2(@Param("openId") String openId);

    Integer selectCountByOpenid3(@Param("openId") String openId);
}