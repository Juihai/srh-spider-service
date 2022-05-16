package com.shenruihai.spider.exception;


import com.shenruihai.spider.log.SpiderLogger;

import java.util.Arrays;

/**
 *
 * @author shenruihai
 * @date 2022/5/16
 */
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        if (t instanceof Exception) {
            SpiderLogger.errorLog.error("Uncaught exception is Thread {}, msg:{} st: {})",
                    thread.getName(), t.getMessage(), Arrays.toString(t.getStackTrace()));
        } else {
            SpiderLogger.errorLog.error("Uncaught exception is Thread {}, msg:{} st: {})",
                    thread.getName(), t.getMessage(), Arrays.toString(t.getStackTrace()));
        }
    }
}
