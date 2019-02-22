package com.dao;

import com.pojo.MmSysUserOrders;

public interface MmSysUserOrdersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysUserOrders record);

    int insertSelective(MmSysUserOrders record);

    MmSysUserOrders selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysUserOrders record);

    int updateByPrimaryKey(MmSysUserOrders record);

    Double selectFlag0ByUserId(Integer uid);

    Double selectFlag3ByUserId(Integer uid);
}