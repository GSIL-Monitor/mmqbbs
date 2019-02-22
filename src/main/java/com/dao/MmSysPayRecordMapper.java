package com.dao;

import com.pojo.MmSysPayRecord;

public interface MmSysPayRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysPayRecord record);

    int insertSelective(MmSysPayRecord record);

    MmSysPayRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysPayRecord record);

    int updateByPrimaryKey(MmSysPayRecord record);
}