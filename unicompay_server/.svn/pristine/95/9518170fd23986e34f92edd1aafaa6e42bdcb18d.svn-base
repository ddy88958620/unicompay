package com.chinaunicom.unipay.ws.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MapUtils {

	public static Date getDate(Map map, String key) {
		try {
			String s = getString(map, key);
			if (s == null)
				return null;
			return DateUtils.parseDate(s, new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"});
        } catch (ParseException e) {
			return null;
		}
	}
	
	public static List getList(Map map,String key){
		return (List)map.get(key);
	}

	public static String getString(Map map, String key) {
		if (map.get(key) == null)
			return null;
		
		if((map.get(key)+"").trim().length()==0){
			return null;
		}
		
		if((map.get(key)+"").trim().equals("null")){
			return null;
		}
		
		return (map.get(key)+"").trim();
	}

	public static Integer getInt(Map map, String key) {
		return getInt(map, key, null);
	}

	public static Integer getInt(Map map, String key, Integer defaultValue) {
		Object obj = map.get(key);
		if (obj == null)
			return defaultValue;
		
		if((obj instanceof String)&&((String)obj).trim().length()>0){
			return Integer.valueOf(((String)obj).trim());
		}

		if (obj instanceof Integer) {
			return (Integer) obj;
		}

		if (obj instanceof java.math.BigDecimal) {
			return ((java.math.BigDecimal) obj).intValue();
		}

		return defaultValue;
	}

	public static Double getDouble(Map map, String key,Double defaultValue) {
		Object obj = map.get(key);
		if (obj == null)
			return defaultValue;

		if((obj instanceof String)&&((String)obj).trim().length()>0){
			return Double.valueOf(((String)obj).trim());
		}

		
		if (obj instanceof Double) {
			return (Double) obj;
		}

		if (obj instanceof java.math.BigDecimal) {
			return ((java.math.BigDecimal) obj).doubleValue();
		}

		return defaultValue;
	}
	
	public static Double getDouble(Map map, String key) {
		return getDouble(map,key,null);
	}

}
