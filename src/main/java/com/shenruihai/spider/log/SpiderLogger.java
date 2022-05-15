package com.shenruihai.spider.log;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author shenruihai
 */
public class SpiderLogger {

    public static Logger infoLog = LogManager.getLogger("infoLogger");
    public static Logger debugLog = LogManager.getLogger("debugLogger");
    public static Logger errorLog = LogManager.getLogger("errorLogger");

    public static Logger rawReqLog = LogManager.getLogger("rawReqLogger");
    public static Logger reqLog = LogManager.getLogger("reqLogger");
    public static Logger respLog = LogManager.getLogger("respLogger");
    public static Logger apiReqLog = LogManager.getLogger("apiReqLogger");
    public static Logger refineLog = LogManager.getLogger("refineLogger");

    public static Logger cronRepayLog = LogManager.getLogger("cronRepayLogger");
    public static Logger callbackLog = LogManager.getLogger("callbackLogger");
    public static Logger notifyLog = LogManager.getLogger("notifyLogger");
    public static Logger reportLog = LogManager.getLogger("reportLogger");
    public static Logger mqLog = LogManager.getLogger("mqLogger");
}
