package com.dao;

import com.pojo.MmSysPay;
import org.apache.ibatis.annotations.Param;

public interface MmSysPayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysPay record);

    int insertSelective(MmSysPay record);

    MmSysPay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysPay record);

    int updateByPrimaryKey(MmSysPay record);

    MmSysPay selectByGzhName(@Param("gzhName")String gzhName);
}