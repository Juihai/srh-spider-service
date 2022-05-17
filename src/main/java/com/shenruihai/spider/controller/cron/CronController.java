package com.shenruihai.spider.controller.cron;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shenruihai.spider.exception.ThreadExceptionHandler;
import com.shenruihai.spider.log.SpiderLogger;
import com.shenruihai.spider.service.SpiderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
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

    private final ExecutorService cronExecutorService = new ThreadPoolExecutor(4, 8, 60,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(128),
            new ThreadFactoryBuilder().setUncaughtExceptionHandler(new ThreadExceptionHandler())
                    .setNameFormat("cron-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());


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
        cronExecutorService.execute(() -> {
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
        });

    }

    /**
     * 手动修复数据
     * @param serviceName
     * @param subjectId
     * @return
     */
    @RequestMapping("/fixData")
    public ResponseEntity<String> fixBookData(String serviceName, String subjectId){
        if(StringUtils.isBlank(serviceName)){
            return ResponseEntity.ok("serviceName is null.");
        }
        if(StringUtils.isBlank(subjectId)){
            return ResponseEntity.ok("subjectId is null.");
        }
        SpiderService spiderService = (SpiderService) applicationContext.getBean(serviceName);
        boolean ret = spiderService.spiderDancer(Long.parseLong(subjectId));
        return ResponseEntity.ok("fixBookDate Finished, result: "+ ret);
    }

    private static void sleep(){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
