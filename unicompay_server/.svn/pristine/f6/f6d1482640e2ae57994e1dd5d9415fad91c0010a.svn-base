package com.unicom.sms.queue.handler;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.bean.SgipSubmit;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.task.MessageTask;
import com.unicom.sms.queue.task.SendMessageTask;
import com.unicom.sms.service.SMSService;
import com.unicom.sms.service.sgip.SgipSendSMS;
import com.unicom.sms.util.SMSContants;
import com.unicom.sms.util.SMSUtils;
import org.marker.protocol.sgip.msg.SubmitResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhaofrancis on 15/1/26.
 */
@Service("snedMessageHandler")
public class SendMessageHandler extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageHandler.class);
    private static final Logger SEND_SMS_ERROR_LOGGER = LoggerFactory.getLogger("send_sms_error");

    @Autowired
    private SgipSendSMS sgipSendSMS;

    @Override
    public void handle(MessageTask messageTask) {
        SendMessageTask sendMessageTask = (SendMessageTask)messageTask;
        Map<String, String> spInfoMap = SMSService.getSPInfoMap(messageTask.getSpNumber());
        if (null == spInfoMap || spInfoMap.size() == 0) {
            LOGGER.error("unsupported sp_number");
            SEND_SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(sendMessageTask, new ServiceException("unsupport sp_number")));
            return;
        }


        try {
            SgipSubmit submit = new SgipSubmit(messageTask.getSpNumber());
            submit.setLoginUserName(spInfoMap.get(SMSContants.PARAM_REMOTE_ACCOUNT));
            submit.setLoginPwd(spInfoMap.get(SMSContants.PARAM_REMOTE_PASSWORD));
            submit.setHost(spInfoMap.get(SMSContants.PARAM_REMOTE_IP));
            submit.setPort(Integer.valueOf(spInfoMap.get(SMSContants.PARAM_REMOTE_PORT)));
            submit.setUserNumber(messageTask.getMobilePhone().split(","));
            submit.setMessageContent(messageTask.getContent().getBytes("GBK"));
            submit.setMessageContentLength(messageTask.getContent().getBytes("GBK").length);
            submit.setFreeTime(System.currentTimeMillis());
            SubmitResp submitResp = sgipSendSMS.sendSMS(submit);
//            LOGGER.info(JSONObject.toJSONString(submitResp));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SEND_SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(sendMessageTask, e));
        }
    }
}
