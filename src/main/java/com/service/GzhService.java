package com.service;/**
 * 描述：
 * Created by Liuyg on 2018-12-26 19:14
 */

import com.pojo.MmSysGzh;
import com.pojo.MmSysGzhToken;

import java.util.List;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-26 19:14
 **/

public interface GzhService {
   MmSysGzh selectByPrimaryKey(String gzhname);
   List<MmSysGzh> selectAll();
   MmSysGzhToken selectGzhToken(String gzhname);
   void updateToken(MmSysGzhToken mmSysGzhToken);
}
