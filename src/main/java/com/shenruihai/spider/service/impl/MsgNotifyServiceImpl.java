package com.shenruihai.spider.service.impl;

import com.shenruihai.spider.log.SpiderLogger;
import com.shenruihai.spider.service.MsgNotifyService;
import com.shenruihai.spider.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Service
public class MsgNotifyServiceImpl implements MsgNotifyService {

    @Value("${msg.notify.mailServer}")
    public String mailServer;
    @Value("${msg.notify.sender}")
    public String sender;
    @Value("${msg.notify.[password]}")
    public String password;
    @Value("${msg.notify.recipients}")
    public String recipients;


    @Override
    public void notify(String title, String content) {
        try{
            EmailUtil.send(title, content, mailServer, sender, password, recipients);
        } catch (GeneralSecurityException exception){
            SpiderLogger.errorLog.error("msgNotify GeneralSecurityException :"+exception);
        }
    }

    @Override
    public void notifyDev(String content){
        String title = "spiderDancer异常通知";
        try{
            EmailUtil.send(title, content, mailServer, sender, password, recipients);
        } catch (GeneralSecurityException exception){
            SpiderLogger.errorLog.error("msgNotify GeneralSecurityException :"+exception);
        }
    }

    @Override
    public void notifyOp(String content){
        String title = "spiderDancer消息通知";
        try{
            EmailUtil.send(title, content, mailServer, sender, password, recipients);
        } catch (GeneralSecurityException exception){
            SpiderLogger.errorLog.error("msgNotify GeneralSecurityException :"+exception);
        }
    }

}
