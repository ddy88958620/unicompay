package com.chinaunicom.unipay.ws.commons;

import com.chinaunicom.unipay.ws.persistence.Notify;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhushuai on 2014/11/28.
 */
@Service
public class Task {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ICPService cps;

    @Resource
    private RedisUtil ru;

    @PostConstruct
    public void init() {
        Scheduler s = new Scheduler();
        s.schedule("*/30 * * * *", new Runnable() {
            @Override
            public void run() {
            if(ru.lock("cp_notify_syn_job")) {
                try {
                    List<Notify> list = Notify.dao.getNotifyByWork();
                    for (Notify notify : list) {
                        ICPService.Notification n = new ICPService.Notification();
                        n.setSendtype(1);
                        n.setAppid(notify.getAppid());
                        n.setConsumecode(notify.getConsumecode());
                        n.setCpid(notify.getCpid());
                        n.setCporderid(notify.getOrderid_3rd());
                        n.setOrderid(notify.getOrderid());
                        n.setFid(notify.getFid());
                        n.setOrdertime(notify.getOrdertime());
                        n.setPayfee(String.valueOf(notify.getPayfee()));
                        n.setStatus(Integer.parseInt(notify.getHret()));
                        try {
                            cps.sendNotification(n);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } finally {
                    ru.unlock("cp_notify_syn_job");
                }
            }
            }
        });
        s.start();
    }

}
