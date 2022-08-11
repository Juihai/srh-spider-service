package com.shenruihai.spider.service;

import com.shenruihai.spider.TestBase;
import com.shenruihai.spider.dao.model.DoubanBook;
import com.shenruihai.spider.log.SpiderLogger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 测试类
 * @author shenruihai
 * @date 2022/5/15
 */
public class SpiderServiceTest extends TestBase {

    @Autowired
    ApplicationContext applicationContext;

    private int failedLimit = 20;

    @Test
    public void mainTest() throws Exception {
        SpiderService spiderService = (SpiderService) applicationContext.getBean("bookService");
        long subjectId = spiderService.findTopByOrderByIdDesc();
        long exeCuteId = subjectId +1;
        long failedCount = 0;
        while(true){
            boolean isExist = spiderService.isExistInfo(exeCuteId);
            if(!isExist){
                boolean result = spiderService.spiderDancer(exeCuteId);
                failedCount = result ? 0 : failedCount+1;
                sleep();
            }
            if(failedCount>= failedLimit){
                throw new Exception("失败次数过多，请检查");
            }
            exeCuteId +=1;
        }
    }

    private static void sleep(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fixTest(){
        SpiderService spiderService = (SpiderService) applicationContext.getBean("bookService");
        long exeCuteId = 25798623;
        boolean isExist = spiderService.isExistInfo(exeCuteId);
        if(isExist){
            System.out.println("该数据已完成入库");
            return;
        }
        boolean result = spiderService.spiderDancer(exeCuteId);
        if(!result){
            SpiderLogger.errorLog.error("获取数据失败, subjectId: "+exeCuteId);
        }else {
            System.out.println("执行结束，结果："+result);
        }
    }

}
