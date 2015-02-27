package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.unipay.ws.commons.Constant;
import com.chinaunicom.unipay.ws.commons.ErrorCode;
import com.chinaunicom.unipay.ws.commons.PropertiesConstant;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.CheckPoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.IPointService;
import com.chinaunicom.unipay.ws.services.PointException;
import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * User: Frank
 * Date: 2014/10/30
 * Time: 14:42
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class PointController extends WSController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    ICPService cps;

    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    IMessageService ms;

    @Resource
    private IPointService ips;

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");

    private final String  pointRate = prop.get("point_rate");

    static class PointResponse extends Response<PointResponse> {

        public Data data = new Data();

        public PointResponse(int result, String uuid) {
            this.data.result = result;
            this.data.uuid = uuid;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data extends Response<Data>{
            private String uuid;
            private int result;

            public Data(){}
            public Data(int result, String uuid) {
                this.result = result;
                this.uuid = uuid;
            }
            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public int getResult() {
                return result;
            }

            public void setResult(int result) {
                this.result = result;
            }
        }

    }
    public void pointPay() throws Exception{

        long time = System.currentTimeMillis();

        PointRequest pr = getJSONObject(PointRequest.class);

        String userId = pr.getIdentityid();
        String appId = pr.getAppid();
        String consumeCode = pr.getConsumecode();

        //入库交易临时信息
        logger.debug("订单入库……");
        Order order = createOrder(pr);
        String orderid = order.getOrderid();
        logger.debug("[" + orderid + "]订单入库成功");

        //查询计费点信息
        ChargePoint chargePoint = ChargePoint.dao.getByConsumecode(consumeCode);
        if (chargePoint == null) {
            logger.info("pointPay >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + ";userId=" + userId + ";appId=" +appId + ";errMsg=" + ErrorCode.POINT_ERROR_CODE_200001);
            throw new PointException(Integer.parseInt(ErrorCode.POINT_CODE_200001.getValue()), orderid);
        }

        logger.info("pointPay >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + "" +
                ";userId=" + userId + ";appId=" + appId + ";consumeType=" + Integer.parseInt(chargePoint.getPointtype()));
        CheckPoint checkPoint = null;
        //判断是否关卡计费点 //道具：1，关卡：2，按次代缴：3，包月：4
        if (Integer.parseInt(chargePoint.getPointtype()) == 2) {
            //是关卡支付，查询数据库，判断是否用户之前记过费
            checkPoint = CheckPoint.dao.getCheckPoint(consumeCode,pr.getIdentityid());
            if (checkPoint != null) {
                logger.info("pointPay >>>> checkPoint!=null >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + ";userId=" + userId + ";appId=" + appId + ";rspCode=" + Constant.ERROR_CODE_300001);
                throw new PointException(Integer.parseInt(ErrorCode.POINT_CODE_300001.getValue()),orderid);
            }
        }

        //计费点金额转换积分
        String pointRate = PropertiesConstant.POINT_RATE;//兑换预设比例
        float rate = Float.valueOf(pointRate);
        int totalPoint = (int) (rate*chargePoint.getPointvalue());//计费点兑换积分比例*计费点金额

        //账户积分需要参数
        String accessToken = pr.getAccesstoken();//访问令牌
        String clientId = pr.getClientid();//客户端ID
        String clientSecret = pr.getClientsecret();//客户端密钥
        String msgId = pr.getMsgid();//客户端流水号

        JSONObject s_Body = new JSONObject();
        s_Body.put("user_id", pr.getIdentityid());
        s_Body.put("consumer_seq", orderid);//支付订单号
        s_Body.put("total_point", totalPoint);
        s_Body.put("goods_name", chargePoint.getPointname());//计费点名称
        s_Body.put("goods_num", "1");

        //MD安全码签名
        String secureCd = MD5.MD5Encode(clientId + clientSecret + msgId + s_Body.toString() + accessToken).toLowerCase();

        JSONObject pointJson = new JSONObject();
        pointJson.put("secure_cd", secureCd);
        pointJson.put("s_body", s_Body);

        JSONObject body = new JSONObject();
        body.put("point_consume", pointJson);

        logger.info("pointPay >>>> body="+body+";accessToken="+accessToken+";clientId="+clientId+";clientSecret="+clientSecret+";msgId="+msgId+";orderid="+orderid+";userId="+pr.getIdentityid()+";appId="+pr.getAppid());

        IPointService.PointResponse ipr = ips.consumePoint(accessToken,clientId,clientSecret,msgId,body);
        Response res = null;
        //订单更新
        logger.debug("[" +orderid + "]进行订单结果更新……");
        order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(ipr.isSucess() ? String.valueOf(ipr.getConsumeid()) : UUID.randomUUID().toString().replace("-",""))
                    .setStatus(ipr.isSucess() ? 0 : 1)
                    .setPayresult(ipr.isSucess() ? "0" : "1").setErrorcode(ipr.isSucess() ? null : ipr.getCode());
            order.update();
            logger.debug("[" +orderid + "]订单结果更新成功");
        } else {
            logger.debug("[" + orderid + "]无法在订单库中找到");
        }
        if(ipr.isSucess()){
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
            cps.sendNotification(n);

            //通知消息系统
            IMessageService.Message msg = new IMessageService.Message();
            msg.setCpid(order.getCpid());
            msg.setPayresult(order.getPayresult());
            msg.setOrderid(order.getOrderid());
            msg.setCporderid(order.getOrderid_3rd());
            msg.setPayfee(order.getPayfee());
            msg.setPaytime(order.getPaytime());
            msg.setPaytype(14);
            msg.setServiceid(order.getServicekey());
            msg.setStatus(order.getStatus());
            ms.notify(msg);

            res = new PointResponse(0,orderid);

            //用户关卡消费记录入库 1、关卡计费点 2、关卡消费记录为空
            if(Integer.parseInt(chargePoint.getPointtype())==2 && checkPoint==null){
                CheckPoint cp = new CheckPoint();
                cp.setAppid(appId);
                cp.setConsumecode(consumeCode);
                cp.setCreatetime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
                cp.setUserid(userId);
                cp.setUuid(orderid);

                logger.info("pointPay >>>> insertLogCheckPoint >>>> consumeCode="+consumeCode+";orderid="+orderid+";userId="+userId+";appId="+appId+";consumeId="+ipr.getConsumeid());
                cp.save();
            }
        }else {
            throw new PointException(Integer.parseInt(ErrorCode.POINT_CODE_+ipr.getCode()),orderid);
        }
        renderJson(res);
    }
    public void pointPayNew() throws Exception {

        long time = System.currentTimeMillis();

        PointRequest pr = getJSONObject(PointRequest.class);

        String userId = pr.getIdentityid();
        String appId = pr.getAppid();
        String cpOrderId = pr.getCporderid();
        String consumeCode = pr.getConsumecode();

       /* //订单校验
        logger.debug("[CP->" + cpOrderId + "]进行CP订单校验……");
        if(!cps.checkOrder(consumeCode, pr.getCpid(), cpOrderId)){
            throw new UnipayException(UN_CPORDER_VALIDATE, "CP订单校验失败", cpOrderId);
        }*/

        //入库交易临时信息
        logger.debug("[CP->" + cpOrderId + "]订单入库……");
        Order order = createOrder(pr);
        String orderid = order.getOrderid();
        logger.debug("[" + orderid + "]订单入库成功");

        //查询计费点信息
        ChargePoint chargePoint = ChargePoint.dao.getByConsumecode(consumeCode);
        if (chargePoint == null) {
            logger.info("pointPayNew >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + ";userId=" + userId + ";appId=" +appId + ";errMsg=" + ErrorCode.POINT_ERROR_CODE_200001);
            throw new PointException.Data(ErrorCode.getCode(ErrorCode.POINT_CODE_,"200001"), orderid);
        }

        logger.info("pointPayNew >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + ";userId=" + userId + ";appId=" + appId + ";consumeType=" + Integer.parseInt(chargePoint.getPointtype()));
        CheckPoint checkPoint = null;
        //判断是否关卡计费点 //道具：1，关卡：2，按次代缴：3，包月：4
        if (Integer.parseInt(chargePoint.getPointtype()) == 2) {
            //是关卡支付，查询数据库，判断是否用户之前记过费
            checkPoint = CheckPoint.dao.getCheckPoint(consumeCode,pr.getIdentityid());
            if (checkPoint != null) {
                logger.info("pointPayNew >>>> logCheckPoint!=null >>>> consumeCode=" + consumeCode + ";orderid=" + orderid + ";userId=" + userId + ";appId=" + appId + ";rspCode=" + Constant.ERROR_CODE_300001);
                throw new PointException.Data(ErrorCode.getCode(ErrorCode.POINT_CODE_,"300001"),orderid);
            }
        }

        //计费点金额转换积分
        String pointRate = PropertiesConstant.POINT_RATE;//兑换预设比例
        float rate = Float.valueOf(pointRate);
        int totalPoint = (int) (rate*chargePoint.getPointvalue());//计费点兑换积分比例*计费点金额

        //账户积分需要参数
        String accessToken = pr.getAccesstoken();//访问令牌
        String clientId = pr.getClientid();//客户端ID
        String clientSecret = pr.getClientsecret();//客户端密钥
        String msgId = pr.getMsgid();//客户端流水号

        JSONObject s_Body = new JSONObject();
        s_Body.put("user_id", pr.getIdentityid());
        s_Body.put("consumer_seq", orderid);//支付订单号
        s_Body.put("total_point", totalPoint);
        s_Body.put("goods_name", chargePoint.getPointname());//计费点名称
        s_Body.put("goods_num", "1");

        //MD安全码签名
        String secureCd = MD5.MD5Encode(clientId + clientSecret + msgId + s_Body.toString() + accessToken).toLowerCase();

        JSONObject pointJson = new JSONObject();
        pointJson.put("secure_cd", secureCd);
        pointJson.put("s_body", s_Body);

        JSONObject body = new JSONObject();
        body.put("point_consume", pointJson);

        logger.info("pointPayNew >>>> body="+body+";accessToken="+accessToken+";clientId="+clientId+";clientSecret="+clientSecret+";msgId="+msgId+";orderid="+orderid+";userId="+pr.getIdentityid()+";appId="+pr.getAppid());

        IPointService.PointResponse ipr = ips.consumePoint(accessToken,clientId,clientSecret,msgId,body);
        Response res = null;
        //订单更新
        logger.debug("[" +orderid + "]进行订单结果更新……");
        order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(ipr.isSucess() ? String.valueOf(ipr.getConsumeid()) : UUID.randomUUID().toString().replace("-",""))
                    .setStatus(ipr.isSucess() ? 0 : 1)
                    .setPayresult(ipr.isSucess() ? "0" : "1").setErrorcode(ipr.isSucess() ? null : ipr.getCode());
            order.update();
            logger.debug("[" +orderid + "]订单结果更新成功");
        } else {
            logger.debug("[" + orderid + "]无法在订单库中找到");
        }
        if(ipr.isSucess()){
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
            n.setPaytype(0);
            cps.sendNotification(n);

            //通知消息系统
            IMessageService.Message msg = new IMessageService.Message();
            msg.setCpid(order.getCpid());
            msg.setPayresult(order.getPayresult());
            msg.setOrderid(order.getOrderid());
            msg.setCporderid(order.getOrderid_3rd());
            msg.setPayfee(order.getPayfee());
            msg.setPaytime(order.getPaytime());
            msg.setPaytype(IMessageService.Message.PayType.WOBI.getValue());
            msg.setServiceid(order.getServicekey());
            msg.setStatus(order.getStatus());
            ms.notify(msg);

            res = new PointResponse.Data(0,orderid);

            //用户关卡消费记录入库 1、关卡计费点 2、关卡消费记录为空
            if(Integer.parseInt(chargePoint.getPointtype())==2 && checkPoint==null){
                CheckPoint cp = new CheckPoint();
                cp.setAppid(appId);
                cp.setConsumecode(consumeCode);
                cp.setCreatetime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
                cp.setUserid(userId);
                cp.setUuid(orderid);

                logger.info("pointPayNew >>>> insertLogCheckPoint >>>> consumeCode="+consumeCode+";orderid="+orderid+";userId="+userId+";appId="+appId+";consumeId="+ipr.getConsumeid());
                cp.save();
            }
        }else {
            throw new PointException.Data(ErrorCode.getCode(ErrorCode.POINT_CODE_,ipr.getCode()),orderid);
        }

        renderJson(res);
    }

    private Order createOrder(PointRequest pr) throws Exception {

        String consumecode = pr.getConsumecode();
        String cpid = pr.getCpid();
        String channelid = pr.getChannelid();

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setEncryptparam("14");
        o.setOrderid_3rd(pr.getCporderid());
        o.setOrdertime(pr.getOrdertime());
        o.setServicekey(pr.getServiceid());
        o.setImsi(pr.getImsi());
        o.setUseraccount(pr.getIdentityid());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(pr.getSdkversion());
        o.setChannelid(channelid);
        o.setEmpno(pr.getAssistantid());
        o.setCpid(cpid);
        o.setPointid(consumecode);

        if(cps.checkAuth(consumecode, cpid, channelid)){
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

    static class PointRequest extends SDKRequest {

        private String appid;	//应用编号

        private String mobile;	//用户手机号

        private String accesstoken;	//账户访问令牌

        private String clientid;	//账户分配客户端id

        private String clientsecret;	//客户端秘钥

        private String msgid;	//客户端流水号

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }
        public void setApp_id(String appid) {
            this.appid = appid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAccesstoken() {
            return accesstoken;
        }

        public void setAccesstoken(String accesstoken) {
            this.accesstoken = accesstoken;
        }
        public void setAccess_token(String accesstoken) {
            this.accesstoken = accesstoken;
        }

        public String getClientid() {
            return clientid;
        }

        public void setClientid(String clientid) {
            this.clientid = clientid;
        }
        public void setClient_id(String clientid) {
            this.clientid = clientid;
        }

        public String getClientsecret() {
            return clientsecret;
        }

        public void setClientsecret(String clientsecret) {
            this.clientsecret = clientsecret;
        }
        public void setClient_secret(String clientsecret) {
            this.clientsecret = clientsecret;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }
        public void setMsg_id(String msgid) {
            this.msgid = msgid;
        }

    }
}
