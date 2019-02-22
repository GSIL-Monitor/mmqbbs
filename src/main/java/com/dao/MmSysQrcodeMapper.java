package com.dao;

import com.pojo.MmSysQrcode;

public interface MmSysQrcodeMapper {
    int deleteByPrimaryKey(String gzhname);

    int insert(MmSysQrcode record);

    int insertSelective(MmSysQrcode record);

    MmSysQrcode selectByPrimaryKey(String gzhname);

    int updateByPrimaryKeySelective(MmSysQrcode record);

    int updateByPrimaryKey(MmSysQrcode record);
}