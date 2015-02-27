package com.unicom.vac.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaofrancis on 15/2/11.
 */
public class UnicomVACStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnicomVACStatusService.class);

    private static final long CLEAN_MAP_INTERVAL_MILLI_SECONDS = 30 * 60 * 1000L;
    private static final int SEND_INTERVAL_MILLI_SECONDS = 60;
    private static final int REQUEST_TIMEOUT_SECONDS = 60;
    private static final int MAX_ERROR_TIMES = 3;
    private static final Timer timer = new Timer();

    private UnicomVACService unicomVACService;

    public UnicomVACStatusService(UnicomVACService unicomVACService) {
        this.unicomVACService = unicomVACService;
        UnicomVACStatusService.timer.scheduleAtFixedRate(new MapClear(), 0L, UnicomVACStatusService.CLEAN_MAP_INTERVAL_MILLI_SECONDS);
        UnicomVACStatusService.timer.scheduleAtFixedRate(new StatusHandler(unicomVACService), 0L, UnicomVACStatusService.SEND_INTERVAL_MILLI_SECONDS);
    }

    private class MapClear extends TimerTask {
        @Override
        public void run() {
            UnicomVACService.cleanMessageLockMap();
        }
    }

    private class StatusHandler extends TimerTask {
        private UnicomVACService unicomVACService;

        public StatusHandler(UnicomVACService unicomVACService) {
            this.unicomVACService = unicomVACService;
        }

        @Override
        public void run() {
            int errorTime = 0;

            if (UnicomVACService.isBusy()) {
                return;
            }
            while (errorTime < UnicomVACStatusService.MAX_ERROR_TIMES) {
                LOGGER.info("check status");
                if (!this.unicomVACService.sendHandset(UnicomVACStatusService.REQUEST_TIMEOUT_SECONDS)) {
                    errorTime++;
                } else {
                    errorTime = 0;
                    break;
                }
            }

            if (errorTime >= 3) {
                this.unicomVACService.unbind();
            }
        }
    }
}
