package com.unicom.tv.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil
{
	
	private DateUtil(){
		
	}

	// 初始化日志
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_PATTERN_M = "yyyy-MM-dd HH:mm:ss";


	/**
	 * dd/MM/yyyy HH:mm:ss
	 */
	public static final String DATE_PATTERN_N = "dd/MM/yyyy HH:mm:ss";

	/**
	 * yyyy-MM-dd
	 */
	public static final String DATE_PATTERN_D = "yyyy-MM-dd";

	public static final String DATE_PATTERN_DD = "yyyyMMdd";
	
	/**
	 * yyyy-MM
	 */
	public static final String DATE_PATTERN_MON = "yyyy-MM";
	
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String DATA_PATTERN_MINI = "yyyyMMddHHmmss";

	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final String DATE_PATTERN_MM = "yyyy-MM-dd HH:mm";

	/**
	 * yyyy年MM月dd日 HH时mm分
	 */
	public static final String DATE_PATTERN_FULL_TIME = "yyyy年MM月dd日 HH时mm分";
	
	/**
	 * yyyy年MM月dd日
	 */
	public static final String DATE_PATTERN_DAY_TIME = "yyyy年MM月dd日";

	public static Date format(String dateStr) {
		Date date = null;
		if (dateStr == null || "".equals(dateStr.trim())) {
			return date;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_D);

		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.debug("日期格式不正确 yyyy-MM-dd", e);
			date = null;
		}
		return date;
	}

	/**
	 * 将date字符串转化成指定pattern的Date时间 默认为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date format(String dateStr, String pattern) {
		Date date = null;
		SimpleDateFormat sdf = null;
		if (dateStr == null || "".equals(dateStr.trim())) {
			return date;
		}
		if (null != pattern && !pattern.equals("")) {
			sdf = new SimpleDateFormat(pattern);
		} else {
			sdf = new SimpleDateFormat(DATE_PATTERN_D);
		}

		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.debug("日期格式不正确 yyyy-MM-dd", e);
			date = null;
		}
		return date;
	}

	/**
	 * 将date字符串转化成指定targetPattern的Date时间字符串 默认为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String format(String dateStr, String srcPattern, String targetPattern) {
		Date date = null;
		SimpleDateFormat sdf = null;
		if (dateStr == null || "".equals(dateStr.trim())) {
			return "";
		}
		if (null != srcPattern && !srcPattern.equals("")) {
			sdf = new SimpleDateFormat(srcPattern);
		} else {
			sdf = new SimpleDateFormat(DATE_PATTERN_D);
		}

		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.debug("日期格式不正确 yyyy-MM-dd", e);
			date = null;
		}
		return format(date, targetPattern);
	}

	public static String format(Date date) {
		String dateStr = null;
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_D);
		dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * 根据日期模式，返回需要的日期对象
	 * 
	 * @param date
	 * @param pattern
	 * @return String
	 */
	public static String format(Date date, String pattern) {
		String dateStr = null;
		if (date == null) {
			return null;
		}
		pattern = StringUtils.isNotBlank(pattern) ? pattern : DATE_PATTERN_D;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * 日期加一天的方法
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOne(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1
		c.add(Calendar.DATE, 1);
		// 结果
		return c.getTime();
	}

	/**
	 * 当前时间增加小时数的方法
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMoreHourTime(Integer hourNum) {
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(new Date());
		// 加小时数
		c.add(Calendar.HOUR, hourNum);
		// 结果
		return c.getTime();
	}

	/**
	 * 日期加一个月的方法
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonth(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1
		c.add(Calendar.MONTH, 1);
		// 结果
		// V2.8 将失效日期设为每月最后一天
		// add by lixg on 2010-09-10
		// 月份加1，得到下个月，
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		c.add(Calendar.DATE, -1);// 再减一天，得到上月最后一天
		return c.getTime();
	}

	/**
	 * 日期加N个月的方法(n月后最后一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonth(Date date, int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1
		c.add(Calendar.MONTH, 1);
		// 结果
		// V2.8 将失效日期设为每月最后一天
		// add by lixg on 2010-09-10
		// 月份加1，得到下个月，
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		c.add(Calendar.DATE, -1);// 再减一天，得到上月最后一天
		return c.getTime();
	}

	/**
	 * 日期加N个月的方法(n月后最后一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonth(String date, int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(format(date));
		// 日期加1
		c.add(Calendar.MONTH, 1);
		// 结果
		// V2.8 将失效日期设为每月最后一天
		// add by lixg on 2010-09-10
		// 月份加1，得到下个月，
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		c.add(Calendar.DATE, -1);// 再减一天，得到上月最后一天
		return c.getTime();
	}
	
	/**
	 * 日期加N个月的方法(n月后最后一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonth(String date, int n,String formatStyle) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(format(date,formatStyle));
		// 日期加1
		c.add(Calendar.MONTH, 1);
		// 结果
		// V2.8 将失效日期设为每月最后一天
		// add by lixg on 2010-09-10
		// 月份加1，得到下个月，
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		c.add(Calendar.DATE, -1);// 再减一天，得到上月最后一天
		return c.getTime();
	}
	
	/**
	 * 日期加N个月的方法(n月后第一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonthFirstDay(String date, int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(format(date));
		// 月份加n
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		return c.getTime();
	}
	
	
	/**
	 * 获取日期获取周一的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final Date getFirstDateOfWeekForDate(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return calendar.getTime();
	}


	/**
	 * 获取日期获取本周最后一天的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final Date getLastDateOfWeekForDate(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		if (1 == calendar.get(Calendar.DAY_OF_WEEK)) {
			return date;
		}
		calendar.add(Calendar.DAY_OF_WEEK, 7);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return calendar.getTime();
	}
	
	
	
	/**
	 * 获取日期获取周一的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final String getFirstDateOfWeek(Date date) {
		return getFirstDateOfWeek(date, DATE_PATTERN_D);
	}

	/**
	 * 获取日期获取周一的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final String getFirstDateOfWeek(Date date, String format) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return format(calendar.getTime(), format);
	}

	/**
	 * 获取日期获取本周最后一天的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final String getLastDateOfWeek(Date date) {
		return getLastDateOfWeek(date, DATE_PATTERN_D);
	}

	/**
	 * 获取日期获取本周最后一天的日期
	 * 
	 * @param date
	 * @return String
	 */
	public static final String getLastDateOfWeek(Date date, String format) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		if (1 == calendar.get(Calendar.DAY_OF_WEEK)) {
			return format(date, format);
		}
		calendar.add(Calendar.DAY_OF_WEEK, 7);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return format(calendar.getTime(), format);
	}
	
	/**
	 * 日期加N个月的方法(n月后第一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonthFirstDay(String date, int n,String formatStyle) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(format(date,formatStyle));
		// 月份加n
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		return c.getTime();
	}

	/**
	 * 日期加N个月的方法(n月后第一天)
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneMonthFirstDay(Date date, int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 月份加n
		c.add(Calendar.MONTH, n);
		c.set(Calendar.DATE, 1);// 将日期设为当月1号，得到当月第一天
		return c.getTime();
	}
	
	/**
	 * 获取日期间间隔绝对值
	 * @return  int
	 */
	public static int getMonthGap(Date date,Date desDate){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int srcDate = c.get(Calendar.MONTH);
		c.setTime(desDate);
		int descDate = c.get(Calendar.MONTH);
		return Math.abs(srcDate-descDate);
	}

	/**
	 * 取日期当月第一天
	 * 
	 * @author FANG_HONG_BIN
	 * @param date
	 * @return
	 */
	public static Date getCurrMonthBeginDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 取日期当月第一天
		c.set(Calendar.DAY_OF_MONTH, 1);
		// 结果
		return c.getTime();
	}

	/**
	 * 取日期下月第一天
	 * 
	 * @author FANG_HONG_BIN
	 * @param date
	 * @return
	 */
	public static Date getNextMonthBeginDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1月
		c.add(Calendar.MONTH, 1);
		// 取日期当月第一天
		c.set(Calendar.DAY_OF_MONTH, 1);
		// 结果
		return c.getTime();
	}

	/**
	 * 取日期当月最后一天
	 * 
	 * @author FANG_HONG_BIN
	 * @param date
	 * @return
	 */
	public static Date getCurrMonthEndDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 取日期当月第一天
		c.set(Calendar.DAY_OF_MONTH, 1);
		// 取日期当月最后一天
		c.roll(Calendar.DAY_OF_MONTH, -1);
		// 结果
		return c.getTime();
	}
	
	/**
	 * 取日期n月最后一天
	 * 
	 * @author FANG_HONG_BIN
	 * @param date
	 * @return
	 */
	public static Date getCurrMonthEndDate(Date date,int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		c.add(Calendar.MONTH, n);
		// 取日期当月第一天
		c.set(Calendar.DAY_OF_MONTH, 1);
		// 取日期当月最后一天
		c.roll(Calendar.DAY_OF_MONTH,-1);
		// 结果
		return c.getTime();
	}

	/**
	 * 日期减一个月的方法
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateCutOneMonth(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1
		c.add(Calendar.MONTH, -1);
		// 结果
		return c.getTime();
	}

	/**
	 * 日期加一年的方法
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddOneYear(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1年
		// c.add(Calendar.DATE , 1);
		c.add(Calendar.YEAR, 1);
		// 结果
		return c.getTime();
	}

	/**
	 * 获取n年后的年底
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAddYearLast(Date date, int n) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		// 日期加1年
		// c.add(Calendar.DATE , 1);
		c.add(Calendar.YEAR, n);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DATE, 31);
		// 结果
		return c.getTime();
	}

	/**
	 * 获取当前时间的字符串,格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @author FANG_HONG_BIN
	 * @return
	 */
	public static String getCurrTimeString() {
		return format(new Date(), DATE_PATTERN_M);
	}

	/**
	 * java.util.Date 转换成 java.sql.Date
	 * 
	 * @author FANG_HONG_BIN
	 * @param date
	 * @return
	 */
	public static java.sql.Date toSqlDate(Date date) {
		java.sql.Date sqlDate = null;
		if (date != null) {
			sqlDate = new java.sql.Date(date.getTime());
		}
		return sqlDate;
	}

	public static boolean isDateType(String dateStr,String pattern){
		return format(dateStr, pattern)!=null;
	}

	public static boolean isDateType(String dateStr){
		return format(dateStr, DATE_PATTERN_M)!=null;
	}

	public static boolean isDateTypeIgnoreNull(String dateStr){
		if(StringUtils.isBlank(dateStr)){
			return true;
		}
		return isDateType(dateStr);
	}

	public static boolean checkSameMonth(Date date1,Date date2){
		if(date1 == null || date2 == null){
			return false;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date1);
		int y1 = c.get(Calendar.YEAR);
		int d1 = c.get(Calendar.MONTH);
		c.setTime(date2);
		int y2 = c.get(Calendar.YEAR);
		int d2 = c.get(Calendar.MONTH);
		return y1==y2&&d1==d2;
	}

	public static boolean checkLargeThanMonth(Date date1,Date date2){
		if(null == date1 || null == date2){
			return false;
		}
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date1);
		int y1 = c.get(Calendar.YEAR);
		int d1 = c.get(Calendar.MONTH);
		c.setTime(date2);
		int y2 = c.get(Calendar.YEAR);
		int d2 = c.get(Calendar.MONTH);
		return y1==y2&&d1>d2;
	}


	/**
	 * 获取日期的年份
	 * @param date
	 * @return
	 */
	public static int getDateYear(Date date){
		if (date == null) {
			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
	
	
	public static Date addDay(Date date,Integer days)
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, +days);
		return calendar.getTime();
	}
}
