package com.chinaunicom.unipay.ws.renders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Frank
 * Date: 2014/5/8
 * Time: 15:52
 */
public class XMLRender extends Render {

    private final static Logger logger = LoggerFactory.getLogger(XMLRender.class);
    private final static XmlMapper mapper = new XmlMapper();

    private static final String contentType = "application/json;charset=" + getEncoding();
    private String XMLText;

    public XMLRender() {
    }

    public XMLRender(Object o) {
        if (o == null)
            throw new IllegalArgumentException("The parameter object can not be null.");
        try {
            this.XMLText = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
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
            writer.write(XMLText);
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
