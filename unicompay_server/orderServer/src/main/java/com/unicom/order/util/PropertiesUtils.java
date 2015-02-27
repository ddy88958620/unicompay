package com.unicom.order.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
//    private static void loadProperties() {
//        try {
//            InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream("innerservice.properties");
////            InputStream is = new BufferedInputStream(new FileInputStream("src/main/resources/innerservice.properties"));
//            properties = new Properties();
//            properties.load(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static String getProperty(String fileName, String key) {
        Properties properties = new Properties();
        try {
            InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }
}
