package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IHttpService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.IPointService;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2015/1/22 0022.
 */
@Service
public class PointService  implements IPointService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    ICPService cps;
    @Resource
    IHttpService ihs;
    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    IMessageService ms;

    private final static Prop prop = PropKit.use("common_config.properties", "utf-8");

    private final String point_consume_addr = prop.get("point_consume_addr");

    @Override
    public PointResponse consumePoint(String accessToken,String clientId,String clientSecret,String msgId,JSONObject body) throws Exception {

        PointResponse pr = new PointResponse();
        Map header = new HashMap();
        header.put("access_token", accessToken);
        header.put("client_id", clientId);
        header.put("client_secret", clientSecret);
        header.put("msg_id", msgId);

        String res = ihs.httpPost(point_consume_addr,body.toJSONString(),header,"UTF-8");
        logger.debug(res);

        JSONObject pointConsume = JSONObject.parseObject(res);

        JSONObject consume = pointConsume.getJSONObject("point_consume");
        String consumeid = null;
        if(consume != null) {
            consumeid = consume.getString("consume_id");
        }else {
            consume = pointConsume.getJSONObject("sys_error");
        }
        String errcd = consume.getString("err_cd");
        String errmsg = consume.getString("err_msg");
        if( StringUtils.isEmpty(consumeid)){
            pr.setCode(errcd);
            pr.setMsg(errmsg);
        }else {
            pr.setConsumeid(Integer.parseInt(consumeid));
        }
        VerifyUtil.logprint("返回数据：",pr);
        return pr;
    }

}
