package com.dao;

import com.pojo.MmSysCopys;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MmSysCopysMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmSysCopys record);

    int insertSelective(MmSysCopys record);

    MmSysCopys selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmSysCopys record);

    int updateByPrimaryKey(MmSysCopys record);

    List<MmSysCopys> seleteByGzhName(String gzhName);

    MmSysCopys seleteByGzhNameAndKeywords(@Param("gzhName") String gzhName,@Param("keywords") String keywords);

}