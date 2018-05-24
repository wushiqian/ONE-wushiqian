package com.wushiqian.util;

import java.util.Calendar;

public class TimeUtil {

    public static String getTimeByCalendar(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH);//获取月份
        month += 1;
        int day = cal.get(Calendar.DATE);//获取日
        String date = year + "-" + month + "-" + day;
        return date;
    }

    public TimeUtil(){}//不可实例化

}
