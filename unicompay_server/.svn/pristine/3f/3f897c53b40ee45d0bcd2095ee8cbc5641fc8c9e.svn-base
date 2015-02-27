package com.chinaunicom.unipay.ws.controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.junit.Test;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2015/1/26
 * Time: 16:55
 */
public class WSControllerTest {

    private static final String XML_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><name>frank</name><dep>pay</dep><request><key name=\"key1\">value1</key><key name=\"key2\">value2</key><key name=\"key3\">value3</key></request></root>";
    private final static XmlMapper mapper = new XmlMapper();

    @JacksonXmlRootElement
    static class Person {
        private String name;
        private String dep;
        private List<Key> request;

        static class Key {
            private String name;
            @JacksonXmlText
            private String value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDep() {
            return dep;
        }

        public void setDep(String dep) {
            this.dep = dep;
        }

        public List<Key> getRequest() {
            return request;
        }

        public void setRequest(List<Key> request) {
            this.request = request;
        }
    }
    @Test
    public void testGetXMLObject() throws Exception {

        StringReader io = new StringReader(XML_CONTENT);
        Person person = mapper.readValue(io, Person.class);

        assertTrue(person.getName().equals("frank"));
    }
}
