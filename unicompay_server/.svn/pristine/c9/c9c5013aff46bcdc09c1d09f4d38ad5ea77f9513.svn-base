package com.unicom.vac.bean;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhaofrancis on 15/2/12.
 */
public class LockMessageBean {
    private CountDownLatch countDownLatch;
    private String responseContent;
    private long createTime;

    public LockMessageBean(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.createTime = System.currentTimeMillis();
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
