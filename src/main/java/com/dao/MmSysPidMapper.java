package com.dao;

import com.pojo.MmSysPid;
import org.apache.ibatis.annotations.Param;

public interface MmSysPidMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysPid record);

    int insertSelective(MmSysPid record);

    MmSysPid selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysPid record);

    int updateByPrimaryKey(MmSysPid record);

    MmSysPid selectPidByGzhName(@Param("gzhName")String gzhName);
}