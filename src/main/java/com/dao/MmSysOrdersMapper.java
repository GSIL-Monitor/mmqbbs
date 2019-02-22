package com.dao;

import com.pojo.MmSysOrders;

import java.util.List;

public interface MmSysOrdersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysOrders record);

    int insertSelective(MmSysOrders record);

    MmSysOrders selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysOrders record);

    int updateByPrimaryKey(MmSysOrders record);

    Integer selectCountByTradeId(MmSysOrders record);

    MmSysOrders selectByTradeId(MmSysOrders record);

}