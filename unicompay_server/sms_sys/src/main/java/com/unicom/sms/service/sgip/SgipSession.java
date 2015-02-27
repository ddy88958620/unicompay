package com.unicom.sms.service.sgip;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.bean.SgipContext;
import com.unicom.sms.bean.SgipSubmit;
import com.unicom.sms.service.SMSCallBackService;
import org.marker.protocol.exception.BindException;
import org.marker.protocol.sgip.Session;
import org.marker.protocol.sgip.conn.Connection;
import org.marker.protocol.sgip.msg.Bind;
import org.marker.protocol.sgip.msg.BindResp;
import org.marker.protocol.sgip.msg.Deliver;
import org.marker.protocol.sgip.msg.Report;
import org.marker.protocol.sgip.msg.SubmitResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaoguoxiang
 */
public class SgipSession extends Session {

    private static final Logger LOGGER = LoggerFactory.getLogger(SgipSession.class);
    
    private SMSCallBackService callbackService;
    
    private SgipContext sgipContext;

    private volatile boolean isLogin = false;
    
    public SgipSession(Connection conn) {
        super(conn);
        sgipContext = new SgipContext();
    }

    public SgipSession(String localUser, String localPass, int localPort, SMSCallBackService callbackService) {
        super(localUser, localPass, localPort);
        sgipContext = new SgipContext();
        this.callbackService = callbackService;
    }

    public SgipSession(String remoteHost, int remotePort) {
        this(new Connection(remoteHost, remotePort));
    }
    
    public SgipSession(String remoteHost, int remotePort, SMSCallBackService callbackService) {
        this(remoteHost, remotePort);
        this.callbackService = callbackService;
    }

    public boolean login(String userName, String pwd) throws BindException, IOException {
        if (isLogin) {
            return isLogin;
        }
        BindResp resp = this.open(new Bind(1, userName, pwd));
        isLogin = resp.getResult() == 0;
        return isLogin;
    }
    
    public SubmitResp sendSubmit(SgipSubmit submit) throws BindException, IOException{
//        if(!isLogin&&!this.isConnected()){
//            this.login(submit.getLoginUserName(), submit.getLoginPwd());
//        }
        try {
            return (SubmitResp) super.sendSubmit(submit);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public void onMessage(Deliver deliver) {
//        System.out.println(deliver.getUserNumber());
//        System.out.println(JSONObject.toJSONString(deliver));
        LOGGER.debug("revice message: " + deliver.getUserNumber());
        if (null != callbackService) {
            sgipContext.setMobilePhone(deliver.getUserNumber());
            sgipContext.setContent(new String(deliver.getContent()));
            sgipContext.setSpNumber(deliver.getSPNumber());
            sgipContext.setReserve(deliver.getReserve());
            callbackService.message(sgipContext);
        }
    }

    @Override
    public void onReport(Report report) {
        LOGGER.debug("send status：" + report.getResult() + " sequence number：" + report.getSubmitSeq()+"  mobile phone："+report.getMobile());
        if (null != callbackService) {
            sgipContext.setResultStatus(report.getResult());
            sgipContext.setSubmitSeq(report.getSubmitSeq());
            callbackService.report(sgipContext);
        }
    }

    @Override
    public void onTerminate() {
        LOGGER.info("SMG break positive");
        if (null != callbackService) {
            callbackService.terminate(sgipContext);
        }
    }
}
