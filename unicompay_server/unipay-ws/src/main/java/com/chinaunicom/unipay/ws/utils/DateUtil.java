package com.chinaunicom.unipay.ws.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @ClassName: DateUtil
 * @Description: 获取时间的工具类
 * @author zhangdw
 * @date 2012-4-10
 */

public class DateUtil {

    /**
      * 功能：取应用服务器日期并以"yyyy-MM-dd HH:mm:ss"格式返回
      */
    public static String getDateTime() {
        return getCurrentDateByFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 功能：取应用服务器日期并以"yyyy-MM-dd格式返回
     */
    public static String getDateStr() {
        return getCurrentDateByFormat("yyyy-MM-dd");
    }

    /**
     * 功能：取应用服务器时间并以"yyyyMMddHHmmss"格式返回
     */
    public static String getDateTimeForLong() {
        return getCurrentDateByFormat("yyyyMMddHHmmss");
    }
    /**
     * 功能：取应用服务器时间并以"yyyyMMddHHmmss"格式返回
     */
    public static String getDateTimeForLongSSS() {
        return getCurrentDateByFormat("yyyyMMddHHmmssSSS");
    }

    /**
     * 功能：取应用服务器时间并以"yyyyMMdd"格式返回
     */
    public static String getDateForLong() {
        return getCurrentDateByFormat("yyyyMMdd");
    }

    /**
     * 功能：取应用服务器时间并以"HHmmss"格式返回
     */
    public static String getTimeForLong() {
        return getCurrentDateByFormat("HHmmss");
    }
    
    /**
     * 功能：取应用服务器时间并以"yyMMdd"格式返回
     */
    public static String getDateForLongShortYear() {
        return getCurrentDateByFormat("yyMMdd");
    }

    /**
     * 功能：取当前服务器时间并以参数指定的格式返回
     */
    public static String getCurrentDateByFormat(String formatStr) {
        long currentTime = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(currentTime);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(formatStr);
        return formatter.format(date);
    }

    /**
     * 功能：转换long型为日期型字串并以"yyyy-MM-dd HH:mm:ss"格式返回
     */
    public static String getDateTime(long al_datetime) {
        java.util.Date date = new java.util.Date(al_datetime);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * 功能：转换long型为日期型字串并以"yyyy-MM-dd HH:mm:ss"格式返回
     */
    public static String getDateHourTime(long al_datetime) {
        java.util.Date date = new java.util.Date(al_datetime);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:00:00");
        return formatter.format(date);
    }

    /**
     * 功能：转换日期型为字串
     */
    public static String getDateString(java.util.Date inDate) {
        return inDate.toString();
    }

    /**
     * 功能：把给定日期与给定天数进行加减运算,返回一个新日期
     */
    public static java.util.Date getDateNDays(java.util.Date date, int days) {//
        if (days > 36500 || days < -36500) {
            return null;
        }
        long l1 = 24, l2 = 60, l3 = 1000, l4 = days;
        long lDays = l1 * l2 * l2 * l3 * l4; //所改变天数的毫秒数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        long lCurrentDate = calendar.getTimeInMillis(); //给定日期的毫秒日期
        long lUpdatedDate = lCurrentDate + lDays; //给定日期与给定天数运算后的毫秒日期
        java.util.Date dateNew = new java.util.Date(lUpdatedDate); //结果日期
        return dateNew;
    }

    /**
     * 功能：把给定日期与给定天数进行加减运算,返回一个yyyyMMdd的新日期
     */

    public static String getDateFromNDays(int days) {
        long currentTime = System.currentTimeMillis();
        Date date = getDateNDays(new Date(currentTime), days);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }

    /**
     * 功能：把给定日期与给定天数进行加减运算,返回一个yyyyMMdd的新日期
     */

    public static String getDateFromNYears(int years) {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
        int day = Integer.parseInt(formatter.format(date));
        day += years * 10000;
        return "" + day;
    }

    /**
     * 得到当前日期(java.sql.Date类型)，注意：没有时间，只有日期
     * @return 当前日期
     */
    public static java.sql.Date getDate() {
        Calendar oneCalendar = Calendar.getInstance();
        return getDate(oneCalendar.get(Calendar.YEAR), oneCalendar.get(Calendar.MONTH) + 1,
            oneCalendar.get(Calendar.DATE));
    }

    /**
     * 根据所给的起始,终止时间来计算间隔天数
     */
    public static int getIntervalDay(java.sql.Date startDate, java.sql.Date endDate) {//
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        int intervalday = (int) interval / (1000 * 60 * 60 * 24);
        return intervalday;
    }

    /**
     * 根据所给年、月、日，得到日期(java.sql.Date类型)，注意：没有时间，只有日期。
     * 年、月、日不合法，会抛IllegalArgumentException(不需要catch)
     */
    public static java.sql.Date getDate(int yyyy, int MM, int dd) {
        if (!verityDate(yyyy, MM, dd)) {
            throw new IllegalArgumentException("This is illegimate date!");
        }

        Calendar oneCalendar = Calendar.getInstance();
        oneCalendar.clear();
        oneCalendar.set(yyyy, MM - 1, dd);
        return new java.sql.Date(oneCalendar.getTime().getTime());
    }

    /**
     * 根据所给的起始,终止时间来计算间隔天数
     */
    public static int getIntervalDay2(Date startDate, Date endDate) {//
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        int intervalday = (int) interval / (1000 * 60 * 60 * 24);
        return intervalday;
    }

    /**
     * 根据所给年、月、日，检验是否为合法日期。
     */
    public static boolean verityDate(int yyyy, int MM, int dd) {//
        boolean flag = false;

        if (MM >= 1 && MM <= 12 && dd >= 1 && dd <= 31) {
            if (MM == 4 || MM == 6 || MM == 9 || MM == 11) {
                if (dd <= 30) {
                    flag = true;
                }
            } else if (MM == 2) {
                if (yyyy % 100 != 0 && yyyy % 4 == 0 || yyyy % 400 == 0) {
                    if (dd <= 29) {
                        flag = true;
                    }
                } else if (dd <= 28) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 功能：根据所给的起始,终止时间来计算间隔月数；
     * @param 入参的格式是：yyyymm或yyyy-mm-dd
     */
    public static int getIntervalMonth(String as_startDate, String as_endDate) throws Exception {
        String ls_startD = "", ls_endD = "";
        Date ld_start = null;
        Date ld_end = null;
        if (as_startDate.length() == 6) {//yyyymm型
            ls_startD = as_startDate.substring(0, 4) + "-"
                        + as_startDate.substring(4, as_startDate.length()) + "-01"; //把yyyymm型的年月日期转换为yyyy-mm型的日期,在后面和-01相加,组成一个合法日期
            ls_endD = as_endDate.substring(0, 4) + "-"
                      + as_endDate.substring(4, as_endDate.length()) + "-01"; //把yyyymm型的年月日期转换为yyyy-mm型的日期,在后面和-01相加,组成一个合法日期
        } else {
            ls_startD = as_startDate;
            ls_endD = as_endDate;
        }
        //.println("as_startD:"+as_startD);    //.println("as_endD:"+as_endD);

        try {
            ld_start = getDate(ls_startD);
            ld_end = getDate(ls_endD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int interval = getIntervalMonth(ld_start, ld_end);
        return interval;
    }

    /**
     * 功能：根据所给的起始,终止时间来计算间隔月数；
     * @param 入参为Date
     */
    public static int getIntervalMonth(java.util.Date startDate, java.util.Date endDate) {
        java.text.SimpleDateFormat mmformatter = new java.text.SimpleDateFormat("MM");
        int monthstart = Integer.parseInt(mmformatter.format(startDate));
        int monthend = Integer.parseInt(mmformatter.format(endDate));
        java.text.SimpleDateFormat yyyyformatter = new java.text.SimpleDateFormat("yyyy");
        int yearstart = Integer.parseInt(yyyyformatter.format(startDate));
        int yearend = Integer.parseInt(yyyyformatter.format(endDate));
        return (yearend - yearstart) * 12 + (monthend - monthstart);
    }

    /**
     * 功能：将入参为"yyyy-mm-dd HH:mm:ss"或"yyyy-mm-dd"形式的字符串转换为Date并进行校验；
     */
    public static java.util.Date getDate(String strdate) throws Exception {
        int yyyy = Integer.parseInt(strdate.substring(0, 4));
        String temp = strdate.substring(5, strdate.length());
        int MM = Integer.parseInt(temp.substring(0, temp.indexOf("-")));
        temp = temp.substring(temp.indexOf("-") + 1, temp.length());
        int dd;
        if (temp.indexOf(" ") > 0) {
            dd = Integer.parseInt(temp.substring(0, temp.indexOf(" ")));
        } else {
            dd = Integer.parseInt(temp);
        }
        if (!verityDate(yyyy, MM, dd)) {
            throw new Exception("非法日期数据");
        }

        java.util.Date d;
        try {
            if (strdate.length() > 10) {

                //d = DateFormat.getDateTimeInstance().parse(strdate);
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
                d = formatter.parse(strdate.substring(0, 19));

            } else {
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
                d = formatter.parse(strdate);
            }
            //      .println(formatter.format(d));
            return d;
        } catch (ParseException e) {
            throw new Exception("日期数据转换错" + e.toString());
        }
    }

    /**
     * 功能：将入参为"yyyyMMdd"形式的字符串转换为Date并进行校验；
     */
    public static java.util.Date getDate2(String strdate) throws Exception {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
        return formatter.parse(strdate);

    }

    public static String formatDate(String strdate, String inFomrat, String outFormat) {
        if (strdate == null || strdate.equals(""))
            return null;
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(inFomrat);
        Date date = null;
        try {
            date = formatter.parse(strdate);
        } catch (ParseException e) {
            //
            //e.printStackTrace();
            return "";
        }
        formatter = new java.text.SimpleDateFormat(outFormat);
        return formatter.format(date);
    }

    /**
     * 获取指定日期的0点0分0秒的时间
     * 
     * @param date
     * @return
     */
    public static java.sql.Timestamp getFirstTime(String date) {
        if (date == null || date.equals("")) {
            return null;
        }
        return java.sql.Timestamp.valueOf(date + " 00:00:00.0");
    }

    /**
     * 获取指定日期的23点59分59秒的时间
     * 
     * @param date
     * @return
     */
    public static java.sql.Timestamp getLastTime(String date) {
        if (date == null || date.equals("")) {
            return null;
        }
        return java.sql.Timestamp.valueOf(date + " 23:59:59.999");
    }

    /**
     * 将日期转换为yyyy-MM-dd格式的字符串
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        return myFormat.format(date);
    }

    /**
     * 将日期转换为指定格式的字符串
     * 
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        return myFormat.format(date);
    }

    /**
     * 将时间转换为yyyy-MM-dd HH:mm:ss格式的字符串
     * 
     * @param time
     * @return
     */
    public static String formatTime(Timestamp time) {
        if (time == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return myFormat.format(time);
    }

    /**
     * 将时间转换为指定格式的字符串
     * 
     * @param time
     * @param format
     * @return
     */
    public static String formatTime(java.sql.Timestamp time, String format) {
        if (time == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        return myFormat.format(time);
    }

    /**
     * 将时间转换为指定格式的字符串
     * 
     * @param time
     * @param format
     * @return
     */
    public static String formatDate(java.sql.Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        return myFormat.format(date);
    }

    public static final Date convertStringToDate(String strDate, String sytle) {
        Locale locale = Locale.CHINESE;
        try {
            return convertStringToDate(sytle, locale, null, strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static final Date convertStringToDate(String pattern, Locale locale, TimeZone zone,
                                                 String strDate) throws ParseException {
        if (locale == null)
            locale = Locale.getDefault();
        if (zone == null)
            zone = TimeZone.getDefault();
        SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
        df.setTimeZone(zone);
        try {
            return df.parse(strDate);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
    }

    /**
     * 获得指定时间的某月的第一天
     * 
     * @param date
     * @return
     * 
     */
    public static Date getMonthFirstDay(Date date) {
        int[] dateArr = getDateArray(date);
        String year = String.valueOf(dateArr[0]);
        String month = String.valueOf(dateArr[1]);
        month = month.length() == 1 ? "0" + month : month;
        Date retDate = convertStringToDate(year + month + "01", "yyyyMMdd");
        return retDate;
    }
    
    /**
     * 当前小时在不在beginHour和endHour时间段
     * 
     * @param date
     * @return
     * 
     */
//    public static boolean comHour1(int hour, int beginHour, int endHour) {
//        int start = beginHour;
//        int end = endHour;
//        if (beginHour > endHour) {
//            start = endHour;
//            end = beginHour;
//            if (hour >= 0 && hour < start) {
//                return true;
//            }
//            if (hour > end && hour <= 24) {
//                return true;
//            }
//            return false;
//        } else {
//            if (hour >= beginHour && beginHour <= endHour) {
//                return true;
//            }
//            return false;
//        }
//
//    }
    
    /**
     * @param hour
     * @param beginHour
     * @param endHour
     * @return
     * @author Jimmy Shan 2013-10-16 modify
     * 判断是否在屏蔽时间段内
     */
    public static boolean comHour(int hour, int beginHour, int endHour) {
    	System.out.println("DateUtil comHour hour-->" + hour + " | beginHour-->" + beginHour + " | endHour-->" + endHour);
        boolean isFee = false;
        if(endHour >= beginHour){
            //当前时间 - 起始时间
            if(hour>endHour || hour<beginHour){
            	isFee = false;//可以计费
            }else{
            	isFee=true;//不可计费
            }
        }else{
        	if((hour>=beginHour && hour<=23) || (hour>=0 && hour<=endHour)){
        		isFee=true;//不可计费
        	}else{
        		isFee = false;//可以计费
        	}
        }
        return isFee;
    }

    /**
     * 获得指定时间的某月的最后一天
     * 
     * @param date
     * @return
     * 
     */
    public static Date getMonthLastDay(Date date) {
        int[] dateArr = getDateArray(date);
        int year = dateArr[0];
        int month = dateArr[1];
        int maxDayOfMonth = getMaxDayOfMonth(year, month);
        String monStr = String.valueOf(month);
        monStr = monStr.length() == 1 ? "0" + monStr : monStr;
        Date retDate = convertStringToDate(
            String.valueOf(year) + String.valueOf(monStr) + String.valueOf(maxDayOfMonth),
            "yyyyMMdd");
        return retDate;
    }

    /**
     * 得到指定日期
     * 
     * @return int[] int[0] 年 int[1] 月 int[2] 日 int[3] 时 int[4] 分 int[5] 秒
     * 
     */
    public static int[] getDateArray(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.CHINA);
        cal.setTime(date);
        int[] dateArr = new int[6];
        dateArr[0] = cal.get(Calendar.YEAR);
        dateArr[1] = cal.get(Calendar.MONTH) + 1;
        dateArr[2] = cal.get(Calendar.DATE);
        dateArr[3] = cal.get(Calendar.HOUR_OF_DAY);
        dateArr[4] = cal.get(Calendar.MINUTE);
        dateArr[5] = cal.get(Calendar.SECOND);
        return dateArr;
    }
    /**
     * 获取指定年和月中该月的最大天数
     * 
     * @param year
     *            指定年
     * @param month
     *            指定月 1-12
     * @return 该月最大天数
     */
    public static int getMaxDayOfMonth(int year, int month) {
        Calendar cal = Calendar
                .getInstance(TimeZone.getDefault(), Locale.CHINA);
        cal.clear();
        cal.set(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    public static void main(String[] args) {
      System.out.println(comHour(12, 10, 11));
    }

}
