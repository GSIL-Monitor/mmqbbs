package com.service.Impl;/**
 * 描述：
 * Created by Liuyg on 2018-12-28 13:16
 */

import com.dao.MmSysSettingMapper;
import com.pojo.MmSysSetting;
import com.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-28 13:16
 **/
@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    private MmSysSettingMapper mmSysSettingMapper;
    @Override
    public MmSysSetting selectByGzhName(String gzhName) {
        return mmSysSettingMapper.selectByGzhName(gzhName);
    }
}
