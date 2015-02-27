package com.unicom.sms.queue.task;

import com.unicom.sms.bean.SgipContext;
import com.unicom.sms.queue.handler.MessageHandler;
import com.unicom.sms.util.MobilePhoneUtil;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public class UpnoticeMessageTask extends MessageTask {
    private long receiveTime;
    private String handlerURL;

    public UpnoticeMessageTask(SgipContext sgipContext, String handlerURL, MessageHandler messageHandler) {
        this.spNumber = sgipContext.getSpNumber();
        this.mobilePhone = MobilePhoneUtil.format(sgipContext.getMobilePhone());
        this.content = sgipContext.getContent();
        this.receiveTime = System.currentTimeMillis();
        this.handlerURL = handlerURL;
        this.handler = messageHandler;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getHandlerURL() {
        return handlerURL;
    }

    public void setHandlerURL(String handlerURL) {
        this.handlerURL = handlerURL;
    }
}
