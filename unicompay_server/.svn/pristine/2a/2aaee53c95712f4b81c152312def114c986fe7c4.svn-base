package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.renders.EncryptJsonRender;
import com.chinaunicom.unipay.ws.renders.EncryptRender;
import com.chinaunicom.unipay.ws.renders.JsonRender;
import com.chinaunicom.unipay.ws.renders.XMLRender;
import com.chinaunicom.unipay.ws.utils.CryptUtil;
import com.chinaunicom.unipay.ws.utils.encrypt.RSA;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.jfinal.render.TextRender;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ResourceBundle;

/**
 * User: Frank
 * Date: 2014/11/7
 * Time: 13:48
 */
public class WSController extends Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static XmlMapper mapper = new XmlMapper();

    private final static ResourceBundle rb = ResourceBundle.getBundle("payapi");
    private final static String SERVER_PRIVATEKEY = rb.getString("encrypt.server_privatekey");

    public <T> T getJSONObject(Class<T> clazz) throws Exception {

        final HttpServletRequest r = getRequest();
        String data = IOUtils.toString(r.getInputStream(), r.getCharacterEncoding());
        final String srckey = r.getParameter("key");

        final String ip = StringUtils.isEmpty(r.getHeader("X-Real-IP")) ? r.getRemoteAddr() : r.getHeader("X-Real-IP");
        if(StringUtils.isEmpty(srckey)
                && !ip.equals("58.247.119.7")
                && !(this instanceof PointController)) {
            logger.debug("非法请求|IP=" + ip);
            throw new Exception("非法请求！");
        }

        if(!StringUtils.isEmpty(srckey)){
            final String key = RSA.decrypt(URLDecoder.decode(srckey, "UTF-8").replace(' ', '+'), SERVER_PRIVATEKEY);
            data = URLDecoder.decode(CryptUtil.decryptBy3DesAndBase64(data, key), "UTF-8");
        }

        logger.debug("获取数据=" + data);
        return JSON.parseObject(data, clazz);

    }

    @Override
    public void renderJson(Object o) {

        String srckey = getPara("key");
        Render render;
        try {
            if(StringUtils.isEmpty(srckey)){
                render = new JsonRender(o);
            }else{
                String key = RSA.decrypt(URLDecoder.decode(srckey, "UTF-8").replace(' ', '+'), SERVER_PRIVATEKEY);
                render = new EncryptJsonRender(o, key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Response rsp;
            if(!StringUtils.isEmpty(srckey)) {
                rsp = new Response(399, "秘钥异常");
            } else {
                rsp = new Response(399, e.getMessage());
            }
            render = new JsonRender(rsp);
        }

        render(render);
    }

    @Override
    public void renderJson(String jsonText) {

        String srckey = getPara("key");
        Render render;
        try {
            if(StringUtils.isEmpty(srckey)){
                render = new TextRender(jsonText);
            }else{
                String key = RSA.decrypt(URLDecoder.decode(srckey, "UTF-8").replace(' ', '+'), SERVER_PRIVATEKEY);
                render = new EncryptRender(jsonText, key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Response rsp;
            if(!StringUtils.isEmpty(srckey)) {
                rsp = new Response(399, "秘钥异常");
            } else {
                rsp = new Response(399, e.getMessage());
            }
            render = new JsonRender(rsp);
        }

        render(render);
    }

    public <T> T getXMLObject(Class<T> clazz) throws IOException {

        HttpServletRequest r = getRequest();
        String input = IOUtils.toString(r.getInputStream(), r.getCharacterEncoding());
        logger.debug(input);

        return mapper.readValue(input, clazz);

    }

    public void renderXML(Object o) {
        render(new XMLRender(o));
    }
}
