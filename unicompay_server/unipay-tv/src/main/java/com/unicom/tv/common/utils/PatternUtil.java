package com.unicom.tv.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
	
	
	public static final String MOBILE_PATTERN = "^1(3[0-9]|4[5,7]|5[0-3,5-9]|8[0,2-9])\\d{8}$";

	public static final boolean match(String content, String pattern) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile(pattern);  
			Matcher m = p.matcher(content);
			flag = m.matches();
		} catch (Exception e) {
		}
		return flag; 
	}
}
