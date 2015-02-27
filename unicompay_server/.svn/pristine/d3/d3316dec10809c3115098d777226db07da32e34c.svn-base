package com.chinaunicom.unipay.ws.commons;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

public class HttpConnectionManager {
    
    static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    static {
        /***构造连接池*/
        connectionManager.setMaxConnectionsPerHost(20);
        // 每个主机的最大并行链接数，默认为2  当前是20
        connectionManager.setMaxTotalConnections(30);
        // maxTotalConnections 客户端总并行链接最大数，默认为20 当前是30
    }
    
    public static int timeout = 6000;

    public static MultiThreadedHttpConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
