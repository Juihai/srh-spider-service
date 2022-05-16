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

    @Test
    public void mainTest(){
        SpiderService spiderService = (SpiderService) applicationContext.getBean("bookService");
        long subjectId = spiderService.findTopByOrderByIdDesc();
        long exeCuteId = subjectId +1;
        while(true){
            boolean result = spiderService.spiderDancer(exeCuteId);
            if(!result){
                SpiderLogger.errorLog.error("获取数据失败, subjectId: "+exeCuteId);
            }else {
                //执行成功，暂停100ms
//                sleep();
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
