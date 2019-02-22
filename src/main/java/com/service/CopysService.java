package com.service;/**
 * 描述：
 * Created by Liuyg on 2019-01-02 19:05
 */

import com.pojo.MmSysCopys;

import java.util.List;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2019-01-02 19:05
 **/

public interface CopysService {
    List<MmSysCopys> selectByGzhName(String gzhName);
    Integer insert(MmSysCopys mmSysCopys);
    MmSysCopys seleteByGzhNameAndKeywords(String gzhName,String keywords);
    Integer updateByPrimaryKey(MmSysCopys record);
}
