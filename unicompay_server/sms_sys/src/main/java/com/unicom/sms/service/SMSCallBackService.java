package com.unicom.sms.service;

import com.unicom.sms.bean.SgipContext;
import com.unicom.sms.exception.ServiceException;


/**
 * Created by zhaofrancis on 15/1/19.
 */
public interface SMSCallBackService {
    public void message(SgipContext sgipContext);
    public void report(SgipContext sgipContext);
    public void terminate(SgipContext sgipContext);

    public void setHandlerUrl(String url);
}
