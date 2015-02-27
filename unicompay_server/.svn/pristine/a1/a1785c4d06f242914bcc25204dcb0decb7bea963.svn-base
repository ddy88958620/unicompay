package com.chinaunicom.unipay.ws.services;

/**
 * Created by Administrator on 2015/1/23 0023.
 */
public class PointException extends RuntimeException {

    public Data data = new Data();

    public PointException(int result, String uuid) {
        this.data.result = result;
        this.data.uuid = uuid;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data extends RuntimeException {
        private String uuid;
        private int result;
        public Data(){}
        public Data(int result, String uuid) {
            this.result = result;
            this.uuid = uuid;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }
}
