package com.shenruihai.spider.controller.cron;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shenruihai.spider.exception.ThreadExceptionHandler;
import com.shenruihai.spider.service.MsgNotifyService;
import com.shenruihai.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    MsgNotifyService msgNotifyService;

    private int failedLimit = 20;

    private final ExecutorService cronExecutorService = new ThreadPoolExecutor(4, 8, 60,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(128),
            new ThreadFactoryBuilder().setUncaughtExceptionHandler(new ThreadExceptionHandler())
                    .setNameFormat("cron-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());


    @RequestMapping("/spiderRun")
    public void spiderRun(String serviceName, Long sleep) throws Exception {
        if(serviceName == null){
            throw new Exception("serviceName is null");
        }
        SpiderService spiderService = (SpiderService) applicationContext.getBean(serviceName);
        long subjectId = spiderService.findTopByOrderByIdDesc();
        if(subjectId < 1){
            throw new Exception("subjectId is invalid, subjectId: "+subjectId);
        }

        cronExecutorService.execute(() -> {
            long exeCuteId = subjectId +1;
            int failedCount = 0;
            while(true){
                boolean isExist = spiderService.isExistInfo(exeCuteId);
                if(!isExist){
                    boolean result = spiderService.spiderDancer(exeCuteId);
                    failedCount = result ? 0 : failedCount+1;
                    if(failedCount>=failedLimit){
                        msgNotifyService.notifyOp("获取数据失败次数过多，请检查后重新开始。 失败subjectId="+subjectId);
                        break;
                    }
                    this.sleep();
                }
                exeCuteId +=1;
            }
        });
    }

    private void sleep(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
