package com.service;

import com.pojo.MmSysSetting;

/**
 * 描述：
 * Created by Liuyg on 2018-12-28 13:16
 */
public interface SettingService {
    MmSysSetting selectByGzhName(String gzhName);
}
