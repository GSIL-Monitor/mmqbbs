package com.dao;

import com.pojo.MmSysGzhToken;

public interface MmSysGzhTokenMapper {
    int deleteByPrimaryKey(String gzhname);

    int insert(MmSysGzhToken record);

    int insertSelective(MmSysGzhToken record);

    MmSysGzhToken selectByPrimaryKey(String gzhname);

    int updateByPrimaryKeySelective(MmSysGzhToken record);

    int updateByPrimaryKey(MmSysGzhToken record);
}