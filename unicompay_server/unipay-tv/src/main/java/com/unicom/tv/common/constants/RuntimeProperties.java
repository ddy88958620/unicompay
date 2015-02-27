package com.unicom.tv.common.constants;

import java.io.IOException;
import java.util.Properties;

public class RuntimeProperties {

	private static Properties properties;
	
	public static synchronized void initProperties(String fileLocation) throws IOException {
		properties = new Properties();
		properties.load(RuntimeProperties.class.getResourceAsStream(fileLocation));
	}
	
	public static String get(String key) {
		if (properties == null) {
			try {
				initProperties("/runtime.properties");
			} catch (IOException e) {
				throw new RuntimeException("can not find runtime.properties");
			}
		}
		
		return properties.getProperty(key);
	}
	
	public static interface RuntimeKey {
		public static final String SYSTEM_PAY_KEY = "system.pay_key";
		public static final String SYSTEM_PAY_KEY_EXPIRES = "system.pay_key_expires";
		public static final String PAY_TV_CODE = "pay.tv_code";
		public static final String PAY_TV_BIND = "pay.tv_bind";
		public static final String PAY_TV_UNBIND = "pay.tv_unbind";
		public static final String PAY_TV_PAY = "pay.tv_pay";
	}
}
