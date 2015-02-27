package com.chinaunicom.unipay.ws.controllers;

import com.chinaunicom.unipay.ws.persistence.BindCard;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.*;
import com.jfinal.aop.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhushuai on 2014/12/17.
 */
@Before({IocInterceptor.class, ExceptionHandler.class})
public class InternalController extends WSController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource IBankService bs;
    public void unBindcard() throws Exception{

        HttpServletRequest request = getRequest();
        String cardno = request.getParameter("cardno");
        String cardtop = cardno.substring(0,6);
        String cardlast = cardno.substring(cardno.length() - 4,cardno.length());
        List<BindCard> clist = BindCard.dao.getByCardno(cardtop,cardlast);

        Response rsp = new Response();
        for (com.chinaunicom.unipay.ws.persistence.BindCard ci : clist) {
            logger.debug("解绑卡……，bindid：" + ci.getCardid() + ", identityid：" + ci.getIdentityId());
            IBankService.UnbindResponse ur = bs.unbindCard(ci.getCardid(), ci.getIdentityId());
            IBankService.BindCardResponse bcl = bs.bindcards(ci.getIdentityId());
            logger.debug("解绑卡成功，bindid：" + ci.getCardid() + ", identityid：" + ci.getIdentityId());
            if (!ur.isSuccess()){
                rsp = new Response(ur.toUnipayCode(), ur.getMsg());
            }
        }
        renderJson(rsp);
    }
}
