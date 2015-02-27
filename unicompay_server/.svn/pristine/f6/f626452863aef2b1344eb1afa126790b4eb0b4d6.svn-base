package com.chinaunicom.unipay.ws.renders;

import com.alibaba.fastjson.JSON;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Frank
 * Date: 2014/10/29
 * Time: 14:07
 */
public class JsonRender extends Render {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String contentType = "application/json;charset=" + getEncoding();
    private String jsonText;

    public JsonRender() {

    }

    public JsonRender(Object object) {
        if (object == null)
            throw new IllegalArgumentException("The parameter object can not be null.");
        this.jsonText = JSON.toJSONString(object);
        logger.debug(jsonText);
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
        }
        finally {
            if (writer != null)
                writer.close();
        }

    }
}
