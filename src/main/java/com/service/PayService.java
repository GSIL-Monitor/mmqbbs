package com.service;/**
 * 描述：
 * Created by Liuyg on 2018-12-31 15:11
 */

import com.pojo.MmSysGzh;
import com.pojo.MmSysUser;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-31 15:11
 **/

public interface PayService {
    void sendRedPack(MmSysGzh mmSysGzh, MmSysUser mmSysUser);
}
