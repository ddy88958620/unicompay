package com.unicom.sms.service;

import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.util.SMSSendRequest;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public interface SMSSendService {
    public void sendSMS(SMSSendRequest smsSendRequest) throws ServiceException;
}
