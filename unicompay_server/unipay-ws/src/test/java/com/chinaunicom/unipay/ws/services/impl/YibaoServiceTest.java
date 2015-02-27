package com.chinaunicom.unipay.ws.services.impl;

import com.chinaunicom.unipay.ws.services.IBankService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2015/1/28
 * Time: 15:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/chinaunicom/unipay/ws/services/impl/ctx.xml")
public class YibaoServiceTest {

    @Resource
    IBankService bs;

    @Test
    public void testCardinfo() throws Exception {

        IBankService.CardResponse cr = bs.cardinfo("4392250014606833");

        assertTrue(cr != null && cr.isSuccess() && StringUtils.isNoneEmpty(cr.getBankname()));

    }

    @Test
    public void testBindList() throws Exception {

        IBankService.BindCardResponse bcr = bs.bindcards("2852900173768045936");

        assertTrue(bcr != null);
    }

}
