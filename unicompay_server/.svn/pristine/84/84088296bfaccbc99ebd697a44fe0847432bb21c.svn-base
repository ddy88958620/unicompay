package com.unicom.sms.service.sgip;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.bean.SgipSubmit;
import com.unicom.sms.exception.ServiceException;
import org.marker.protocol.sgip.msg.SubmitResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhaofrancis on 15/1/26.
 */
@Service
public class SgipSendSMS {
    private static final Logger LOGGER = LoggerFactory.getLogger(SgipSendSMS.class);

    private SgipSession sgipSession;

    public synchronized SubmitResp sendSMS(SgipSubmit submit) throws ServiceException {
        try {
            return this.getSgipSession(submit).sendSubmit(submit);
        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private SgipSession getSgipSession(SgipSubmit submit) throws ServiceException {
        if (null == sgipSession) {
//            LOGGER.info("init sgip session: " + JSONObject.toJSONString(submit));
            sgipSession = new SgipSession(submit.getHost(), submit.getPort());

            try {
                sgipSession.login(submit.getLoginUserName(), submit.getLoginPwd());
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            }
        }
        return sgipSession;
    }

}
