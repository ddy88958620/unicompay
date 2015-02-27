package com.unicom.sms.queue.handler;

import com.unicom.sms.bean.SMSBean;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.service.http.HttpService;
import com.unicom.sms.queue.task.MessageTask;
import com.unicom.sms.queue.task.UpnoticeMessageTask;
import com.unicom.sms.util.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhaofrancis on 15/1/23.
 */
@Service("upnoticeMessageHandler")
public class UpnoticeMessageHandler extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnoticeMessageHandler.class);
    private static final Logger SMS_ERROR_LOGGER = LoggerFactory.getLogger("sms_error");

    @Override
    public void handle(MessageTask messageTask) {
        UpnoticeMessageTask upnoticeMessageTask = (UpnoticeMessageTask)messageTask;
        SMSBean smsBean = new SMSBean(upnoticeMessageTask);
        try {
            HttpService.sendRequest(smsBean);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(smsBean, e));
        }
    }
}
