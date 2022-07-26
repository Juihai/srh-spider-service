package com.shenruihai.spider.service;

import com.shenruihai.spider.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  测试邮件发送
 * @author shenruihai
 * @date 2022/7/26
 */
public class SpiderMsgServiceTest extends TestBase {

    @Autowired
    private MsgNotifyService msgNotifyService;

    @Test
    public void sendMsgTest(){
        String title = "SpiderDancer消息通知";
        String content = "这是一封测试邮件，请忽略。";
        msgNotifyService.notify(title, content);
    }

}
