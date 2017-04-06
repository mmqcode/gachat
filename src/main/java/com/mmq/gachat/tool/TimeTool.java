package com.mmq.gachat.tool;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/28.
 */
@Component
public class TimeTool {
    public static final String simpleDateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String simpleDate = "yyyy-MM-dd";


    /**
     * 获取当前时间毫秒数
     * @return
     */
    public static Long getCurrentTime(){

        return System.currentTimeMillis();

    }

    /**
     * 获取当前时间秒数
     * @return
     */
    public static Long getCurrentTimeInSeconds(){

        return System.currentTimeMillis()/1000L;

    }


    /**
     * 根据时间毫秒数获取Date对象
     * @param millisSeconds
     * @return
     */
    public static Date getCurrentTimeBySeconds(String millisSeconds){
        try{
            long millisSecond = Long.parseLong(millisSeconds);
            Date date = new Date(millisSecond);
            return date;
        }catch(NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据时间毫秒数获取Date对象
     * @param millisSeconds
     * @return
     */
    public static Date getCurrentTimeBySeconds(Long millisSeconds){
        try{
            Date date = new Date(millisSeconds);
            return date;
        }catch(NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }

    public static long milliSecondsBetweenDates(Date date1,Date date2){
        long interval = date1.getTime()-date2.getTime();
        return interval;
    }


    public static long minutesBetweenDates(Date beginDate, Date endDate){
        Long miliSeconds = milliSecondsBetweenDates(beginDate, endDate);
        return (miliSeconds/1000L)/60L;
    }

    /**
     * 将日期转换为yyyy-MM-dd HH:mm:ss模式
     * @param date
     * @return
     */
    public static String getFormatTime(Date date){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取距离今天指定日期间隔的日期,正数表示往未来的间隔日期，负数表示过去的间隔日期
     * 返回形式为YYYY-MM-DD格式的日期
     * @author mmq 2014年11月13日17:38:48
     * @return String
     */
    public static String getDateByDifferFromToday(int differ){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, differ);
        Date targetDate = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(targetDate);
       //dateString =dateString.substring(0,10);//获取格式为YYYY-MM-DD的日期。
        return dateString;
    }

    public static String getDateByDifferFromCertainDate(String date, int differ){
        Calendar c = Calendar.getInstance();
        c.setTime(TimeTool.str2DateDay(date));
        c.add(Calendar.DAY_OF_MONTH, differ);
        Date targetDate = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(targetDate);
        return dateString;
    }

    public static Date getDateByDifferFromCertainDate(Date date, int differ){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, differ);
        Date targetDate = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(targetDate);
        return str2DateDay(dateString);
    }

    public static Long getMillisBetween(Long differ, long time){
        return time + differ;
    }


    public static Date str2Date(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDateTime);
        Date date = null;
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date str2DateDay(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDate);
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String date2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDateTime);
        if(date == null){
            return null;
        }
        return simpleDateFormat.format(date);
    }

    public static String getDataString(String formatstr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatstr);
        return simpleDateFormat.format(getCalendar().getTime());
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

}
