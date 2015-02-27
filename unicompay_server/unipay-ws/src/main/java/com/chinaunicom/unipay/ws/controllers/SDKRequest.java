package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinaunicom.unipay.ws.services.IBankService;

/**
 * Created by 兵 on 2015/1/8.
 */
public class SDKRequest<R extends SDKRequest> extends Request<R> {
    //CP生成的唯一订单号，最长50位
    private String cporderid;

    //CP订单生成时间
    private String ordertime;

    //服务标示
    private String serviceid;

    //沃商店计费点
    private String consumecode;

    //cpID
    private String cpid;

    //渠道ID
    private String channelid;

    //最长50位，商户生成的用户唯一标识
    private String identityid;

    //IMSI
    private String imsi;

    //0 ：IMEI；1：MAC；2：UUID（针对IOS系统）；3：OTHER
    private Integer terminaltype;

    //最长50位
    private String terminalid;

    //3G加油站营业员编号
    private String assistantid;

    private String sdkversion;

    public String getCporderid() {
        return cporderid;
    }

    public void setCporderid(String cporderid) {
        this.cporderid = cporderid;
    }

    public String getServiceid() {

        IBankService.VaCodeResponse vaCodeResponse = new IBankService.VaCodeResponse();

        return serviceid;


    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }
    public void setService_id(String service_id) { this.serviceid = service_id; }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getIdentityid() {
        return identityid;
    }

    public void setIdentityid(String identityid) {
        this.identityid = identityid;
    }

    public Integer getTerminaltype() {
        return terminaltype;
    }

    public void setTerminaltype(Integer terminaltype) {
        this.terminaltype = terminaltype;
    }

    public String getTerminalid() {
        return terminalid;
    }

    public void setTerminalid(String terminalid) {
        this.terminalid = terminalid;
    }

    public String getConsumecode() {
        return consumecode;
    }

    public void setConsumecode(String consumecode) {
        this.consumecode = consumecode;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getAssistantid() {
        return assistantid;
    }

    public void setAssistantid(String assistantid) {
        this.assistantid = assistantid;
    }

    public String getSdkversion() {
        return sdkversion;
    }

    public void setSdkversion(String sdkversion) {
        this.sdkversion = sdkversion;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }
}
