package com.unicom.sms.service.impl;

import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.handler.MessageHandler;
import com.unicom.sms.queue.local.LocalMessageQueue;
import com.unicom.sms.queue.task.SendMessageTask;
import com.unicom.sms.service.SMSSendService;
import com.unicom.sms.util.SMSContants;
import com.unicom.sms.util.SMSSendRequest;
import com.unicom.sms.util.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhaofrancis on 15/1/23.
 */
@Service
public class SMSSendServiceImpl implements SMSSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSSendServiceImpl.class);
    private static final Logger SEND_SMS_LOGGER = LoggerFactory.getLogger("send_sms");
    private static final Logger SEND_SMS_ERROR_LOGGER = LoggerFactory.getLogger("send_sms_error");

    @Resource(name = "snedMessageHandler")
    private MessageHandler sendMessageHandler;

    @Override
    public void sendSMS(SMSSendRequest smsSendRequest) throws ServiceException {
        this.checkAccess(smsSendRequest.getClientId(), smsSendRequest.getClientSecret(), smsSendRequest.getSpNumber());

        SendMessageTask messageTask = new SendMessageTask();
        messageTask.setContent(smsSendRequest.getContent());
        messageTask.setMobilePhone(smsSendRequest.getMobilePhone());
        messageTask.setSpNumber(smsSendRequest.getSpNumber());
        messageTask.setHandler(sendMessageHandler);

        SEND_SMS_LOGGER.info(SMSUtils.formatSMS(messageTask));

        try {
            LocalMessageQueue.put(messageTask);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            SEND_SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(messageTask, e));
            throw new ServiceException(SMSContants.QUEUE_ERROR_MESSAGE, SMSContants.QUEUE_ERROR_CODE);
        }
    }

    private void checkAccess(String clientId, String clientSecret, String spNumber) {
        //TODO

    }
}
