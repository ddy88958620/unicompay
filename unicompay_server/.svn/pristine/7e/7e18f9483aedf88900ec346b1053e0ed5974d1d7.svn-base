package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;

/**
 * User: Frank
 * Date: 2015/1/7
 * Time: 17:11
 */
class Response<R extends Response> {

    private int code;
    private String msg;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
    public R parse(String data) {
        return (R)JSON.parseObject(data, getClass());
    }

    public Response() {
        code = 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
