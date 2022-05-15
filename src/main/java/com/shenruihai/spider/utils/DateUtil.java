package com.shenruihai.spider.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author shenruihai
 * @date 2022/5/14
 */
public class DateUtil {

    public static final String DEFAULT_PATTERN_1 = "yyyy-MM-dd";
    public static final String DEFAULT_PATTERN_2 = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_PATTERN_3 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_PATTERN_4 = "yyyyMMdd";

    public static String format(Date date, String pattern) {
        if(null == date){
            return null;
        }
        return DateFormatUtils.format(date, pattern);
    }

    public static String format(Date date) {
        if(null == date){
            return null;
        }
        return format(date, DEFAULT_PATTERN_1);
    }

    public static Date parse(String dateStr, String... patterns) {
        if(null == dateStr){
            return null;
        }
        try {
            return DateUtils.parseDate(dateStr, patterns);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parse(String date) {
        return parse(date, DEFAULT_PATTERN_1, DEFAULT_PATTERN_2, DEFAULT_PATTERN_3, DEFAULT_PATTERN_4);
    }

    public static boolean isValidDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static Date today() {
        return DateUtils.truncate(new Date(), Calendar.DATE);
    }

    public static String todayStr() {
        return format(new Date());
    }

    public static Date yesterday() {
        return DateUtils.addDays(today(), -1);
    }

    public static Date tomorrow() {
        return DateUtils.addDays(today(), 1);
    }

    public static Date addDays(Date date, int days) {
        return DateUtils.addDays(date, days);
    }

    public static Date addSeconds(Date date, int seconds) {
        return DateUtils.addSeconds(date, seconds);
    }

    public static long diffDays(Date startDate, Date endDate) {
        return (DateUtils.truncate(endDate, Calendar.DATE).getTime() - DateUtils.truncate(startDate, Calendar.DATE).getTime()) / (1000L * 60 * 60 * 24);
    }

    public static long diffSeconds(Date startDate, Date endDate) {
        return (DateUtils.truncate(endDate, Calendar.SECOND).getTime() - DateUtils.truncate(startDate, Calendar.SECOND).getTime()) / (1000L);
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime){
        if(localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getDateStartStr(Date date) {
        return format(date) + " 00:00:00";
    }

    public static String getDateEndStr(Date date) {
        return format(date) + " 23:59:59";
    }

    public static boolean afterNow(Date date, Integer tolerantSeconds) {
        if (date == null) {
            return false;
        }
        return date.after(DateUtil.addSeconds(new Date(), tolerantSeconds));
    }

}
