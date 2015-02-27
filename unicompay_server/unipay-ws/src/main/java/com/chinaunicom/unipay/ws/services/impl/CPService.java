package com.chinaunicom.unipay.ws.services.impl;

import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Notify;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.utils.CryptUtil;
import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: Frank
 * Date: 2015/1/9
 * Time: 13:53
 */
@Service
public class CPService implements ICPService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static XmlMapper mapper = new XmlMapper();

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");
    private final static String authurl = prop.get("send.authurl");

    @Resource private CloseableHttpClient c;

    @Override
    public void sendNotification(Notification n) throws Exception {

        final ChargePoint chargingPoint = ChargePoint.dao.getByConsumecode(n.getConsumecode());
        final UserInfo userinfo = UserInfo.dao.getByCpid(n.getCpid());

        final String signMsg = MD5.MD5Encode("orderid=" + n.getCporderid() + "&ordertime=" + n.getOrdertime() + "&cpid=" + n.getCpid() + "&appid=" + n.getAppid() + "&fid=" + n.getFid() + "&consumeCode=" + n.getConsumecode() + "&payfee=" + n.getPayfee() + "&payType=" + n.getPaytype() + "&hRet=" + n.getStatus() + "&status=" + (n.getStatus() == 0 ? "00000" : String.valueOf(n.getStatus())) + "&Key=" + userinfo.getSignup().getSecretkey()).toLowerCase();

        final String request = new StringBuilder()
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><callbackReq>")
            .append("<orderid>").append(n.getCporderid()).append("</orderid>")
            .append("<ordertime>").append(n.getOrdertime()).append("</ordertime>")
            .append("<cpid>").append(n.getCpid()).append("</cpid>")
            .append("<appid>").append(n.getAppid()).append("</appid>")
            .append("<fid>").append(n.getFid()).append("</fid>")
            .append("<consumeCode>").append(n.getConsumecode()).append("</consumeCode>")
            .append("<payfee>").append(n.getPayfee()).append("</payfee>")
            .append("<payType>").append(n.getPaytype()).append("</payType>")
            .append("<hRet>").append(n.getStatus()).append("</hRet>")
            .append("<status>").append(n.getStatus() == 0 ? "00000" : String.valueOf(n.getStatus())).append("</status>")
            .append("<signMsg>").append(signMsg).append("</signMsg>")
            .append("</callbackReq>")
            .toString();

        final String url = chargingPoint.getProduct().getOnlineurl().trim();
        final HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/xml;charset=utf-8");
        post.setEntity(new StringEntity(request));

        final long start = System.currentTimeMillis();
        String rtn = "";
        CloseableHttpResponse response = null;
        try {
            response = c.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                rtn = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            }
            logger.debug("CP通知请求完成|URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms|发送=" + request + "|获取=" + rtn);
            rtn = rtn.substring(rtn.lastIndexOf("<") - 1, rtn.lastIndexOf("<"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(response != null) {
                response.close();
            }
        }

        if (n.getSendtype() == 0) {
            logger.debug("新建CP通知");
            Notify notify = new Notify();
            notify.setOrderid_3rd(n.getCporderid());
            notify.setOrderid(n.getOrderid());
            notify.setOrdertime(Tools.getCurrentTime());
            notify.setLastnotifytime(Tools.getCurrentTime());
            notify.setCpid(n.getCpid());
            notify.setAppid(n.getAppid());
            notify.setFid(n.getFid());
            notify.setConsumecode(n.getConsumecode());
            notify.setHret(String.valueOf(n.getStatus()));
            notify.setCpkey(userinfo.getSignup().getSecretkey());
            notify.setSignmsg(signMsg);
            notify.setRtnurl(chargingPoint.getProduct().getOnlineurl());
            notify.setPayfee(Integer.parseInt(n.getPayfee()));
            notify.setPaytype(n.getPaytype());

            if (rtn.equals("1")) {
                notify.setCprtnval(Integer.parseInt(rtn));
                notify.setStatus(0);
                notify.setIscpreturned(1);
                notify.setSendtimes(1);
                notify.setWorkstatus(0);
            } else {
                notify.setCprtnval(Integer.parseInt(rtn));
                notify.setStatus(1);
                notify.setIscpreturned(0);
                notify.setMaxsendtimes(10);
                notify.setWorkstatus(1);
                notify.setSendtimes(1);
            }
            notify.save();
        } else if (n.getSendtype() == 1) {
            logger.debug("CP通知重发");
            Notify notify = Notify.dao.findById(n.getOrderid());
            if (rtn.equals("1")) {
                notify.setSendtimes(notify.getSendtimes() + 1).setLastnotifytime(Tools.getCurrentTime())
                        .setStatus(0).setCprtnval(Integer.parseInt(rtn)).setIscpreturned(1).setWorkstatus(0).update();
            } else {
                if (notify.getSendtimes() + 1 > 10) {
                    notify.setWorkstatus(0);
                }
                notify.setSendtimes(notify.getSendtimes() + 1).setLastnotifytime(Tools.getCurrentTime()).update();
            }
        }

    }

    @Override
    public boolean checkOrder(String consumecode, String cpid, String cporderid) throws Exception {

        final ChargePoint cp = ChargePoint.dao.getByConsumecode(consumecode);
        final UserInfo ui = UserInfo.dao.getByCpid(cpid);

        final String url = cp.getProduct().getOnlineurl() + "?serviceid=validateorderid";

        final String signMsg = CryptUtil.md5Hex("orderid=" + cporderid + "&Key=" + ui.getSignup().getSecretkey());
        final String request = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<checkOrderIdReq>")
                .append("<orderid>").append(cporderid).append("</orderid>")
                .append("<signMsg>").append(signMsg).append("</signMsg>")
                .append("<usercode></usercode>")
                .append("<provinceid></provinceid>")
                .append("<cityid></cityid>")
                .append("</checkOrderIdReq>")
                .toString();

        final HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/xml;charset=utf-8");
        post.setEntity(new StringEntity(request));
        final long start = System.currentTimeMillis();
        CloseableHttpResponse rsp = null;

        try {
            rsp = c.execute(post);
            CheckOrderResult r;
            String str = "";
            if(rsp.getStatusLine().getStatusCode() == 200) {
                str = IOUtils.toString(rsp.getEntity().getContent(), "utf-8");
                r = mapper.readValue(str, CheckOrderResult.class);
            } else {
                r = new CheckOrderResult();
            }
            logger.debug("CP订单校验请求完成|URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms|发送=" + request + "|获取=" + str);
            return r.isSuccess();
        } finally {
            if(rsp != null) {
                rsp.close();
            }
        }
    }

    @Override
    public boolean checkAuth(String consumecode, String cpid, String channelid) throws Exception {

        final String url = authurl + "&cpId=" + cpid +"&consumeCode=" + consumecode + "&channelid=" + channelid;

        final HttpGet request = new HttpGet(url);
        final long start = System.currentTimeMillis();
        CloseableHttpResponse response = null;
        try {
            response = c.execute(request);
            AuthRsp ar;
            if(response.getStatusLine().getStatusCode() == 200){
                ar = mapper.readValue(IOUtils.toString(response.getEntity().getContent(), "UTF-8"), AuthRsp.class);
            } else {
                ar = new AuthRsp();
            }
            logger.debug("CP鉴权请求完成|URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms");
            return ar.isSuccess();
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    @JacksonXmlRootElement(localName="paymessages")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CheckOrderResult {

        private final static String SUCCESS = "0";

        //0-验证成功 1-验证失败
        private String checkOrderIdRsp;

        public String getCheckOrderIdRsp() {
            return checkOrderIdRsp;
        }

        public void setCheckOrderIdRsp(String checkOrderIdRsp) {
            this.checkOrderIdRsp = checkOrderIdRsp;
        }

        public boolean isSuccess() {
            return SUCCESS.equals(checkOrderIdRsp);
        }
    }

    @JacksonXmlRootElement(localName="authRsp")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AuthRsp {

        private final static String SUCCESS = "0";

        //鉴权结果
        private String hRet;

        public String gethRet() {
            return hRet;
        }

        public void sethRet(String hRet) {
            this.hRet = hRet;
        }

        public boolean isSuccess() {
            return SUCCESS.equals(hRet);
        }
    }
}
