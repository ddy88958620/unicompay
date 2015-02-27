package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chinaunicom.unipay.ws.services.IAliPayService;
import com.chinaunicom.unipay.ws.services.IHttpService;
import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/27 0027.
 */
@Service
public class AliPayService implements IAliPayService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private String aliqrcodeurl = "https://mapi.alipay.com/gateway.do";

    private String aliqrcodekey = "8kxoutl1n8418ku61jrw6lbs19uu7c57";

    private XmlMapper mapper = new XmlMapper();
    @Resource
    IHttpService ihs;
    @Override
    public AliResponse getQrcode(AliPay pay) throws Exception {
        String verify = "_input_charset="+pay.get_input_charset()+(pay.getBiz_data() !=null ? "&biz_data="+JSON.toJSONString(pay.getBiz_data()) : "")+(pay.getBiz_type() != null ? "&biz_type="+pay.getBiz_type() :"")+"&method="+pay.getMethod()+"&partner="+pay.getPartner()+"&service="+pay.getService()+"&timestamp="+pay.getTimestamp();
        String sign = MD5.sign(verify, aliqrcodekey, pay.get_input_charset());
        pay.setSign(sign);
        Map map = urlEncode(pay,pay.get_input_charset());
        String param = "biz_type="+map.get("biz_type")+"&biz_data="+map.get("biz_data")+"&sign_type="+map.get("sign_type")+"&sign="+map.get("sign")+"&timestamp="+map.get("timestamp")+"&_input_charset="+map.get("_input_charset")+"&service="+map.get("service")+"&partner="+map.get("partner")+"&method="+map.get("method");
        String xml = ihs.httpGet(aliqrcodeurl,param,pay.get_input_charset());
        return mapper.readValue(xml, AliResponse.class);
    }

    @Override
    public NotifyResponse returnNotify(Map param,String charset) throws Exception {

        String sign = (String) param.get("sign");
        param.remove("sign");
        String verify = VerifyUtil.getVerify(param);
        NotifyResponse nr = new NotifyResponse();
        if(sign.equals(MD5.sign(verify, aliqrcodekey, charset))){
            nr.setIs_success("T");
        }else {
            nr.setIs_success("F");
            nr.setError_code("PARAM_ILLEGAL");
        }
        return nr;
    }

    private Map<String,String> urlEncode(AliPay pay,String encode) throws Exception{
        Map param = new HashMap();
        param.put("biz_type",URLEncoder.encode(pay.getBiz_type(),encode));
        param.put("biz_data",URLEncoder.encode(JSON.toJSONString(pay.getBiz_data()),encode));
        param.put("sign_type",URLEncoder.encode(pay.getSign_type(),encode));
        param.put("sign",URLEncoder.encode(pay.getSign(),encode));
        param.put("timestamp",URLEncoder.encode(pay.getTimestamp(),encode));
        param.put("_input_charset",URLEncoder.encode(pay.get_input_charset(),encode));
        param.put("service",URLEncoder.encode(pay.getService(), encode));
        param.put("partner", URLEncoder.encode(pay.getPartner(), encode));
        param.put("method",URLEncoder.encode(pay.getMethod(),encode));

        return param;
    }

    public static class MD5 {

        public static String sign(String text, String key, String input_charset) {
            text = text + key;
            return DigestUtils.md5Hex(getContentBytes(text, input_charset));
        }

        public static boolean verify(String text, String sign, String key, String input_charset) {
            text = text + key;
            String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
            if(mysign.equals(sign)) {
                return true;
            }
            else {
                return false;
            }
        }

        private static byte[] getContentBytes(String content, String charset) {
            if (charset == null || "".equals(charset)) {
                return content.getBytes();
            }
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
            }
        }

    }
}
