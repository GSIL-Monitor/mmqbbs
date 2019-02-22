package com.task;

import com.pojo.MmSysGzh;
import com.service.GzhService;
import com.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private GzhService gzhService;
    @Value("${TaskOrderCalculationFlag}")
    private boolean TaskOrderCalculationFlag;

    private boolean orderCalculationflag = false;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟执行一次 实时获取订单
    public void getOrder() {
        if(TaskOrderCalculationFlag){
            return;
        }
        Date date = new Date();
        List<MmSysGzh> list =  gzhService.selectAll();
        Integer i = 0 ;
        for (MmSysGzh mmgzh:list) {
            asyncTaskService.orderGet(mmgzh.getTbsession(),DateUtils.addMinute(date, -20),i);
            i ++ ;
        }
    }

    private static List<MmSysGzh> MmSysGzhlist = null ;
    private static Date date = new Date();
    private static Date date2 = new Date();

    @Scheduled(cron = "0 0 8 21 * ?") // 每月21号上午10点15分触发  获取上个月的订单进行结算
    public void orderCalculationFlag() {
        System.out.println("开始同步");
        if(TaskOrderCalculationFlag){
            MmSysGzhlist  =  gzhService.selectAll();
            date = DateUtils.getLastMonth(date);
            date2 = DateUtils.getMonth(date2);
            date2 = DateUtils.addMinute(date2,20);
            orderCalculationflag = true;
        }
    }

    @Scheduled(cron = "0/10 * * * * ?") // 一秒钟执行一次 获取上个月的订单进行结算 需要用orderCalculationFlag方法初始化并开启
    public void orderCalculation() {
        if(!orderCalculationflag){
            return;
        }
        for (MmSysGzh mmgzh:MmSysGzhlist) {
            asyncTaskService.orderCalculation(mmgzh.getTbsession(),date);
        }
        date = DateUtils.addMinute(date, 20);
        if( date.getTime() >= date2.getTime() ){
            orderCalculationflag = false;
        }
    }
}
