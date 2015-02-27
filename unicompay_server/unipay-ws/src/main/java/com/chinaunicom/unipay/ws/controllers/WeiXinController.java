package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.IWeiXinService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.jfinal.aop.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.TreeMap;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.MINUTE;

/**
 * Created by jackj_000 on 2015/2/10 0010.
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class WeiXinController extends WSController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    IWeiXinService wxs;
    @Resource
    ICPService ics;
    @Resource
    IMessageService ims;
    @Resource
    RedisUtil ru;

    static class WeiXinRequest extends SDKRequest<WeiXinRequest>{

    }
    static class WeiXinResponse extends Response<WeiXinResponse>{
        private String code_url;

        WeiXinResponse(int code, String msg, String code_url) {
            super(code, msg);
            this.code_url = code_url;
        }

        public String getCode_url() {
            return code_url;
        }

        public void setCode_url(String code_url) {
            this.code_url = code_url;
        }
    }

    public void getQrcode() throws Exception{

        WeiXinRequest req = getJSONObject(WeiXinRequest.class);
        String consumecode = req.getConsumecode();

        ChargePoint cp = ChargePoint.dao.getByConsumecode(consumecode);

        Order o = createOrder(req);

        IWeiXinService.IWeiXin wx = new IWeiXinService.IWeiXin();
        wx.setTotal_fee(cp.getPointvalue());
        wx.setProduct_id(cp.getPointid());
        wx.setBody(cp.getPointname());
        wx.setOut_trade_no(o.getOrderid());

        IWeiXinService.IWeiXinResponse wxr = wxs.getQrcode(wx);
        Response res;
        if(wxr.isSuccess()){
            res = new WeiXinResponse(0,"成功获取二维码",wxr.getCode_url());
        }else {
            res = new Response(555,wxr.getErr_code_des());
        }
        VerifyUtil.logprint("返回数据：",res);
        renderJson(res);
    }

    public void callback() throws Exception{

        IWeiXinService.IWeiXinCallBackRequest iwcr = getXMLObject(IWeiXinService.IWeiXinCallBackRequest.class);

        String src = JSON.toJSONString(iwcr,new SimplePropertyPreFilter(IWeiXinService.IWeiXinCallBackRequest.class,"sign"));
        Map map= JSON.parseObject(src,new TypeReference<TreeMap<String,String>>(){});
        String verify = VerifyUtil.getVerify(map);
        boolean flag = verify.equals(iwcr.getSign());
        IWeiXinService.IWeiXinCallBackResponse res;

        if(flag){
            res = new IWeiXinService.IWeiXinCallBackResponse("SUCCESS","OK");
        }else {
            res = new IWeiXinService.IWeiXinCallBackResponse("FAIL","签名失败！");
        }
        renderJson(res);
        if(!flag){
            return;
        }
        VerifyUtil.logprint("回调接口返回数据：",iwcr);
        String orderid = iwcr.getOut_trade_no();
        if(ru.getSet(RedisUtil.Table.CALLBACK.getKey(orderid), "y", 30 * MINUTE) != null) {
            logger.debug("订单[" + orderid + "]重复发送支付结果通知");
            return;
        }

        Order order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(iwcr.getTransaction_id())
                    .setStatus(iwcr.isSuccess() ? 0 : 1)
                    .setPayresult(String.valueOf(iwcr.isSuccess() ? "0" : 1))
                    .setErrorcode(iwcr.isSuccess() ? "0" : "1")
                    .update();
        } else {
            logger.error("[" + orderid + "]无法在订单库中找到");
        }
        //通知CP
        ICPService.Notification n = new ICPService.Notification();
        n.setAppid(order.getProductid());
        n.setConsumecode(order.getPointid());
        n.setCpid(order.getCpid());
        n.setCporderid(order.getOrderid_3rd());
        n.setOrderid(order.getOrderid());
        n.setFid(order.getChannelid());
        n.setOrdertime(order.getPaytime());
        n.setPayfee(String.valueOf(order.getPayfee()));
        n.setStatus(order.getStatus());
        n.setPaytype(6);
        try {
            ics.sendNotification(n);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        //通知消息系统
        IMessageService.Message msg = new IMessageService.Message();
        msg.setCpid(order.getCpid());
        msg.setPayresult(order.getPayresult());
        msg.setOrderid(order.getOrderid());
        msg.setCporderid(order.getOrderid_3rd());
        msg.setPayfee(order.getPayfee());
        msg.setPaytime(order.getPaytime());
        msg.setPaytype(IMessageService.Message.PayType.WEIXIN_QCODE.getValue());
        msg.setServiceid(order.getServicekey());
        msg.setStatus(order.getStatus());
        try {
            ims.notify(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private Order createOrder(WeiXinRequest aqr) throws Exception {

        String consumecode = aqr.getConsumecode();
        String cpid = aqr.getCpid();
        String channelid = aqr.getChannelid();

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setEncryptparam("16");
        o.setOrderid_3rd(aqr.getCporderid());
        o.setOrdertime(aqr.getOrdertime());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(aqr.getSdkversion());
        o.setServicekey(aqr.getServiceid());
        o.setChannelid(channelid);
        o.setCpid(cpid);
        o.setPointid(consumecode);

        if(ics.checkAuth(consumecode, cpid, channelid)){
            UserInfo userInfo = UserInfo.dao.getByCpid(cpid);
            ChargePoint point = ChargePoint.dao.getByConsumecode(consumecode);
            o.setUserindex(userInfo.getUserindex());
            o.setUserid(userInfo.getUserid());
            o.setProductindex(point.getCntindex());
            o.setProductid(point.getProduct().getCntid());
            o.setProductname(point.getProduct().getCntname());
            o.setPointindex(point.getPointindex());
            o.setPointname(point.getPointname());

            o.setPayfee(point.getPointvalue());

        }
        o.save();

        return o;
    }

}
