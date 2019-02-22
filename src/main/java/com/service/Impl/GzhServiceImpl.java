package com.service.Impl;/**
 * 描述：
 * Created by Liuyg on 2018-12-26 19:15
 */

import com.dao.MmSysGzhMapper;
import com.dao.MmSysGzhTokenMapper;
import com.pojo.MmSysGzh;
import com.pojo.MmSysGzhToken;
import com.service.GzhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-26 19:15
 **/
@Service
public class GzhServiceImpl implements GzhService {
    @Autowired
    private MmSysGzhMapper mmSysGzhMapper;
    @Autowired
    private MmSysGzhTokenMapper mmSysGzhTokenMapper;

    @Override
    public MmSysGzh selectByPrimaryKey(String gzhname) {
        return mmSysGzhMapper.selectByPrimaryKey(gzhname);
    }

    @Override
    public List<MmSysGzh> selectAll() {
        return mmSysGzhMapper.selectAll();
    }

    public MmSysGzhToken selectGzhToken(String gzhname){
        return mmSysGzhTokenMapper.selectByPrimaryKey(gzhname);
    }

    @Override
    public void updateToken(MmSysGzhToken mmSysGzhToken) {
        mmSysGzhTokenMapper.updateByPrimaryKey(mmSysGzhToken);
    }
}
