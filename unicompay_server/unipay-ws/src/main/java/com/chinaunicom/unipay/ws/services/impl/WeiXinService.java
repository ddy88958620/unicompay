package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.chinaunicom.unipay.ws.services.IHttpService;
import com.chinaunicom.unipay.ws.services.IWeiXinService;
import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jackj_000 on 2015/2/10 0010.
 */
@Service
public class WeiXinService implements IWeiXinService {

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");
    private XmlMapper mapper = new XmlMapper();
    @Resource
    IHttpService ihs;

    private final String url = prop.get("weixin.url");
    private final String appid = prop.get("weixin.appid");
    private final String mch_id = prop.get("weixin.appid");
    private final String spbill_create_ip = prop.get("weixin.spbill_create_ip");
    private final String notify_url = prop.get("weixin.notifyurl");
    private final String trade_type = "NATIVE";

    @Override
    public IWeiXinResponse getQrcode(IWeiXin wx) throws Exception {

        wx.setAppid(appid);
        wx.setMch_id(mch_id);
        wx.setSpbill_create_ip(spbill_create_ip);
        wx.setNotify_url(notify_url);
        wx.setTrade_type(trade_type);
        wx.setNonce_str(RandomUtil.getRandom(32));
        String src = JSON.toJSONString(wx);
        Map map= JSON.parseObject(src,new TypeReference<TreeMap<String,String>>(){});
        String verify = VerifyUtil.getVerify(map);
        wx.setSign(MD5.MD5Encode(verify));
        String xml = mapper.writeValueAsString(wx);
        String res = ihs.httpPost(url,xml,null,"UTF-8");
        return mapper.readValue(res,IWeiXinResponse.class);

    }
}
