package com.unicom.sms.service.impl;

import com.unicom.sms.bean.SgipContext;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.handler.MessageHandler;
import com.unicom.sms.queue.local.LocalMessageQueue;
import com.unicom.sms.queue.task.UpnoticeMessageTask;
import com.unicom.sms.service.SMSCallBackService;

import com.unicom.sms.util.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaofrancis on 15/1/19.
 */
@Service("SMSCallBackService")
public class SMSCallBackServiceImpl implements SMSCallBackService {
    private static final Logger SMS_LOGGER = LoggerFactory.getLogger("sms");
    private static final Logger SMS_ERROR_LOGGER = LoggerFactory.getLogger("sms_error");

    @Resource(name = "upnoticeMessageHandler")
    private MessageHandler messageHandler;

    private ExecutorService executorService = Executors.newFixedThreadPool(500);
    private String handlerUrl = null;

    @Override
    public void message(SgipContext sgipContext) {
//        SMSBean smsBean = new SMSBean(sgipContext, this.handlerUrl);
//        SMS_LOGGER.info(SMSCallBackServiceImpl.formatSMS(smsBean));

//        System.out.println(sgipContext.getMobilePhone() + "----" + sgipContext.getContent());
//        executorService.submit(new HandeSMSThread(smsBean));


        UpnoticeMessageTask upnoticeMessageTask = new UpnoticeMessageTask(sgipContext, handlerUrl, messageHandler);
        SMS_LOGGER.info(SMSUtils.formatSMS(upnoticeMessageTask));
        try {
            LocalMessageQueue.put(upnoticeMessageTask);
        } catch (ServiceException e) {
            SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(upnoticeMessageTask, e));
        }
    }

    @Override
    public void report(SgipContext sgipContext) {

    }

    @Override
    public void terminate(SgipContext sgipContext) {

    }

    @Override
    public void setHandlerUrl(String url) {
        this.handlerUrl = url;
    }

//    private static class HandeSMSThread implements Callable {
//        private static final Logger LOGGER = LoggerFactory.getLogger(HandeSMSThread.class);
//        private static final Logger SMS_ERROR_LOGGER = LoggerFactory.getLogger("sms_error");
//
//        private SMSBean smsBean;
//
//        public HandeSMSThread(SMSBean smsBean) {
//            this.smsBean = smsBean;
//        }
//
//        public Object call() {
//            try {
//                HttpService.sendRequest(smsBean);
//            } catch (ServiceException e) {
//                LOGGER.error(e.getMessage(), e);
//                SMS_ERROR_LOGGER.error(SMSUtils.formatErrorSMS(smsBean, e));
//            }
//            return null;
//        }
//
//
//
//    }



}
