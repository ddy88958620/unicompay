package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;

/**
 * User: Frank
 * Date: 2015/1/7
 * Time: 17:18
 */
class Request<R extends Request> {
    public String toJSONString() {
        return JSON.toJSONString(this);
    }
    public R parse(String data) {
        return (R)JSON.parseObject(data, getClass());
    }
}
