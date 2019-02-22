package com.dao;

import com.pojo.MmSysGzh;

import java.util.List;

public interface MmSysGzhMapper {
    int deleteByPrimaryKey(String gzhname);

    int insert(MmSysGzh record);

    int insertSelective(MmSysGzh record);

    MmSysGzh selectByPrimaryKey(String gzhname);

    int updateByPrimaryKeySelective(MmSysGzh record);

    int updateByPrimaryKey(MmSysGzh record);

    List<MmSysGzh> selectAll();
}