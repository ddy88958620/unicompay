package com.unicom.sms.bean;

import com.unicom.sms.queue.task.MessageTask;
import com.unicom.sms.queue.task.UpnoticeMessageTask;
import com.unicom.sms.util.MobilePhoneUtil;

/**
 * Created by zhaofrancis on 15/1/22.
 */
public class SMSBean {
    private String spNumber;
    private String mobilePhone;
    private String content;
    private long receiveTime;
    private String handlerURL;

    public SMSBean(){}

//    public SMSBean(SgipContext sgipContext, String handlerURL) {
//        this.spNumber = sgipContext.getSpNumber();
//        this.mobilePhone = MobilePhoneUtil.format(sgipContext.getMobilePhone());
//        this.content = sgipContext.getContent();
//        this.receiveTime = System.currentTimeMillis();
//        this.handlerURL = handlerURL;
//    }
    public SMSBean(UpnoticeMessageTask messageTask) {
        this.spNumber = messageTask.getSpNumber();
        this.mobilePhone = messageTask.getMobilePhone();
        this.content = messageTask.getContent();
        this.receiveTime = messageTask.getReceiveTime();
        this.handlerURL = messageTask.getHandlerURL();
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
