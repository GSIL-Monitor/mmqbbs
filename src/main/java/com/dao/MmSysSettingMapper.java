package com.dao;

import com.pojo.MmSysSetting;
import org.apache.ibatis.annotations.Param;

public interface MmSysSettingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysSetting record);

    int insertSelective(MmSysSetting record);

    MmSysSetting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysSetting record);

    int updateByPrimaryKey(MmSysSetting record);

    MmSysSetting selectByGzhName(@Param("gzhName")String gzhName);
}