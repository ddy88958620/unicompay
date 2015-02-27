package com.chinaunicom.unipay.ws.utils;

import org.junit.Test;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.*;
import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2015/1/21
 * Time: 10:54
 */
public class RedisUtilTest {

    //测试Table枚举获取表名功能
    @Test
    public void testEnumTable() throws Exception {
        assertTrue(Table.PREORDER.getKey("123").equals("preorder:123"));
        assertTrue(Table.PREPAY.getKey("123").equals("prepay:123"));
    }
}
