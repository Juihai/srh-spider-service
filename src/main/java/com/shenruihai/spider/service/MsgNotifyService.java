package com.shenruihai.spider.service;

/**
 * @author shenruihai
 * @date 2022/7/26
 */
public interface MsgNotifyService {

    void notify(String title, String content);

    void notifyDev(String content);

    void notifyOp(String content);

}
