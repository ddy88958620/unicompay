package com.chinaunicom.unipay.ws.services;

/**
 * Created by 兵 on 2014/12/10.
 */
public class PayChannel {
    private String pc_id;
    private String pm_id;
    private String pc_province;
    private String pc_desc;

    public String getPc_id() {
        return pc_id;
    }

    public void setPc_id(String pc_id) {
        this.pc_id = pc_id;
    }

    public String getPm_id() {
        return pm_id;
    }

    public void setPm_id(String pm_id) {
        this.pm_id = pm_id;
    }

    public String getPc_province() {
        return pc_province;
    }

    public void setPc_province(String pc_province) {
        this.pc_province = pc_province;
    }

    public String getPc_desc() {
        return pc_desc;
    }

    public void setPc_desc(String pc_desc) {
        this.pc_desc = pc_desc;
    }
}
