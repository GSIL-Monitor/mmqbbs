package com.service.Impl;/**
 * 描述：
 * Created by Liuyg on 2019-01-02 19:05
 */

import com.dao.MmSysCopysMapper;
import com.pojo.MmSysCopys;
import com.service.CopysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2019-01-02 19:05
 **/

@Service
public class CopysServiceImpl implements CopysService {
    @Autowired
    private MmSysCopysMapper mmSysCopysMapper;

    @Override
    public List<MmSysCopys> selectByGzhName(String gzhName) {
        return mmSysCopysMapper.seleteByGzhName(gzhName);
    }

    @Override
    public Integer insert(MmSysCopys mmSysCopys) {
        return mmSysCopysMapper.insert(mmSysCopys);
    }

    @Override
    public MmSysCopys seleteByGzhNameAndKeywords(String gzhName, String keywords) {
        return mmSysCopysMapper.seleteByGzhNameAndKeywords(gzhName,keywords);
    }

    @Override
    public Integer updateByPrimaryKey(MmSysCopys record) {
        return mmSysCopysMapper.updateByPrimaryKey(record);
    }

}
