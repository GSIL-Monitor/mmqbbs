package com.service;/**
 * 描述：
 * Created by Liuyg on 2018-12-27 18:56
 */

import com.pojo.MmSysUser;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-27 18:56
 **/

public interface UserService {
    MmSysUser selectByOpenid(String openId);
    Integer insert(MmSysUser mmSysUser);
    Integer upPid(MmSysUser mmSysUser);
    Integer selectCountByOpenid1(String openId);
    Integer selectCountByOpenid2(String openId);
    Integer selectCountByOpenid3(String openId);
    Integer updateByPrimaryKeySelective(MmSysUser mmSysUser);
}
