package com.shenruihai.spider.controller;

import com.shenruihai.spider.log.SpiderLogger;
import com.shenruihai.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务
 * @author shenruihai
 * @date 2022/5/15
 */
@RestController
@RequestMapping("/cron")
public class CronController {

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping("/spiderRun")
    public void spiderRun(String serviceName) throws Exception {
        if(serviceName == null){
            throw new Exception("serviceName is null");
        }
        SpiderService spiderService = (SpiderService) applicationContext.getBean(serviceName);

        long subjectId = spiderService.findTopByOrderByIdDesc();
        if(subjectId < 1){
            throw new Exception("subjectId is invalid, subjectId: "+subjectId);
        }
        long exeCuteId = subjectId +1;

        while(true){
            boolean result = spiderService.spiderDancer(exeCuteId);
            if(!result){
                SpiderLogger.errorLog.error("获取数据失败, subjectId: "+exeCuteId);
            }else {
                //执行成功，暂停100ms
                sleep();
            }
            exeCuteId +=1;
        }
    }

    private static void sleep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
