package com.unicom.sms.queue.task;

import com.unicom.sms.queue.handler.MessageHandler;
import com.unicom.sms.util.SMSContants;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public abstract class MessageTask {
    protected String spNumber;
    protected String mobilePhone;
    protected String content;
    protected MessageHandler handler;
//    private long receiveTime;
//    private String handlerURL;

    public void execute() {
        if (null == this.handler) {
            throw new NullPointerException(SMSContants.NULL_HANDLER_ERROR);
        }
        this.handler.execute(this);
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageHandler getHandler() {
        return handler;
    }

    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }
}
