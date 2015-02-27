package com.chinaunicom.unipay.ws.renders;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.utils.CryptUtil;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * User: Frank
 * Date: 2015/1/13
 * Time: 17:06
 */
public class EncryptRender extends Render {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String contentType = "application/json;charset=" + getEncoding();
    private String jsonText;

    public EncryptRender() {
    }

    public EncryptRender(String jsonText, String key) throws Exception {
        if (jsonText == null)
            throw new IllegalArgumentException("The parameter object can not be null.");
        logger.debug(jsonText);
        this.jsonText = CryptUtil.encryptBy3DesAndBase64(URLEncoder.encode(jsonText, "UTF-8"), key);
    }

    @Override
    public void render() {

        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setContentType(contentType);
            writer = response.getWriter();
            writer.write(jsonText);
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
