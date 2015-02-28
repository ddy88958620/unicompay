package com.chinaunicom.unipay.ws.commons;

/**
 * Created by jackj_000 on 2015/2/28 0028.
 */
public enum ConsumeCode {
    CONSUME_CODE_("");
    private String value;

    private ConsumeCode(String value){this.value = value;}

    private String getValue(){return value;}

}
