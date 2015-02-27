package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.unipay.ws.services.IChargeService;
import com.chinaunicom.unipay.ws.services.PayChannel;
import com.chinaunicom.unipay.ws.utils.XMLUtils;
import com.chinaunicom.unipay.ws.utils.pay19.CipherUtil;
import com.chinaunicom.unipay.ws.utils.pay19.KeyedDigestMD5;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Administrator on 2015/1/21 0021.
 */
@Service
public class ChargeService implements IChargeService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceBundle resb1 = ResourceBundle.getBundle("payapi");

    private final String URLPAYCHANNEL = "http://change.19ego.cn/channel.jsp";

    private final String URL19PAY = "http://change.19ego.cn/pgworder/orderdirect.do";

    private final String URL19PAYBALANCE = "http://change.19ego.cn/query/balance";
    private final String pay19key = resb1.getString("pay19.pay19key");

    private final String notifyurl = resb1.getString("pay19.notifyurl");

    @Resource
    private CloseableHttpClient httpClient;

    @Override
    public ChargeResponse charge(Charge charge) throws Exception {
        logger.info("充值卡支付请求："+URL19PAY);
        CipherUtil util = new CipherUtil();
        String cardnum1= util.encryptData(charge.getCardnum1(),pay19key);
        String cardnum2=util.encryptData(charge.getCardnum2(),pay19key);

        String verifystring= "version_id="+charge.getVersionid()+"&merchant_id="+charge.getMerchantid()+"&order_date="+charge.getOrderdate()+"&order_id="+charge.getOrderid()+
                "&amount="+charge.getAmount()+"&currency="+charge.getCurrency()+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+"&pm_id="+charge.getPmid()+
                "&pc_id="+charge.getPcid()+"&merchant_key="+pay19key;	//商户代码源串
        verifystring = KeyedDigestMD5.getKeyedDigest(verifystring, "");

        String param = "version_id="+charge.getVersionid()+"&merchant_id="+charge.getMerchantid()+"&order_date="+charge.getOrderdate()+"&order_id="+charge.getOrderid()+
                "&amount="+charge.getAmount()+"&currency="+charge.getCurrency()+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+"&pm_id="+charge.getPmid()+
                "&pc_id="+charge.getPcid()+"&verifystring="+verifystring+"&order_pdesc="+charge.getOrderpdesc()+"&select_amount="
                +charge.getSelectamount()+"&notify_url="+notifyurl;

        StringBuffer responseMessage = null;
        URLConnection connection = null;
        URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;

        responseMessage = new StringBuffer();
        reqUrl = new URL(URL19PAY);
        connection = reqUrl.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(300*1000);
        connection.setReadTimeout(300*1000);

        reqOut = new OutputStreamWriter(connection.getOutputStream());

        reqOut.write(param);
        reqOut.flush();

        int charCount = -1;
        in = connection.getInputStream();
        br = new BufferedReader(new InputStreamReader(in,
                "GBK"));
        while ( (charCount = br.read()) != -1) {
            responseMessage.append( (char) charCount);
        }
        in.close();
        reqOut.close();
        String info = responseMessage.toString();


        return XMLUtils.getXmlObject(info, ChargeResponse.class);
    }

    public List<PayChannel> getChannel(String merchantid) throws Exception {
        String param = "merchant_id="+merchantid;
        List<PayChannel> list = new ArrayList<>();
        String res = httpGet(URLPAYCHANNEL, param,"gbk");
        String[] info = StringUtils.trim(res).split("\\|");
        for(int j=0;j<info.length;j+=4) {
            PayChannel payChannel = new PayChannel();
            payChannel.setPc_id(info[j]);
            payChannel.setPm_id(info[j + 1]);
            payChannel.setPc_province(info[j + 2]);
            payChannel.setPc_desc(info[j + 3]);
            list.add(payChannel);
        }
        return list;
    }

    public Map<String, Object> queryBalance(String version,String merchantid,String pcid,String cardnum,String pwd ) throws Exception {
        Map map = new HashMap();
        CipherUtil util = new CipherUtil();
        String cardnum1= util.encryptData(cardnum, pay19key);
        String cardnum2=util.encryptData(pwd,pay19key);

        String verifystring= "version="+version+"&mxid="+merchantid+"&pcid="+pcid+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+
                "&key="+pay19key;	//商户代码源串
        verifystring = KeyedDigestMD5.getKeyedDigest(verifystring, "");

        String param = "version="+version+"&mxid="+merchantid+ "&pcid="+pcid+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+
                "&verify="+verifystring;

        String res = httpGet(URL19PAYBALANCE,param,"gbk");
        String result[] = res.split("&");
        for(String str : result){
            String value[] = str.split("=");
            if(value.length == 1) {
                map.put(value[0],"");
                return map;
            }
            map.put(value[0], value[1]);

        }
        return map;
    }

    public String pointCharge(Map<String,String> map) throws Exception {
        Map headermap = new HashMap();
        headermap.put("client_id",map.get("clientid"));
        headermap.put("client_secret",map.get("clientsecret"));
        headermap.put("Content-Type","application/json;charset=utf-8");

        String json = null;
        JSONObject sbody = new JSONObject();
        sbody.put("user_id",map.get("identityid"));
        sbody.put("point_count",Integer.parseInt(map.get("amount")));

        JSONObject datasrc = new JSONObject();
        datasrc.put("service_id",map.get("serviceid"));

        sbody.put("data_src",datasrc);
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("recharge_point_shen_zhou",sbody);

        String res = httpPost(map.get("rechargeurl"),jsonObject.toString(),headermap,"UTF-8");
        logger.info("沃币充值结果："+res);

        return res;
    }

    public String httpPost(String url,String param,Map<String,String> map,String encode) throws Exception{
        String res = null;
        HttpPost request = new HttpPost(url);
        for(Map.Entry<String,String> entry : map.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        request.setEntity(new StringEntity(param));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                res = IOUtils.toString(response.getEntity().getContent(), encode);
            } else {
                res = response.getStatusLine().getStatusCode() + "";
            }
            logger.debug("请求返回状态码：" + response.getStatusLine().getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            response.close();
        }
        return res;
    }

    public String httpGet(String url,String param,String encode) throws Exception{
        String res = null;
        HttpGet request = new HttpGet(url+"?"+param);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                res = IOUtils.toString(response.getEntity().getContent(), encode);
            } else {
                res = response.getStatusLine().getStatusCode() + "";
            }
            logger.debug("请求返回状态码：" + response.getStatusLine().getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            response.close();
        }
        return res;
    }
}
