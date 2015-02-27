package com.chinaunicom.unipay.ws.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

/**
 * Created by 兵 on 2014/12/10.
 */
public class XMLUtils {
    private final static XmlMapper mapper = new XmlMapper();
    public static <T> T getXmlObject(String xml,Class<T> clazz) throws IOException {
        return  mapper.readValue(xml,clazz);
    }
}
