package com.unicom.vac.service;

import com.alibaba.fastjson.JSONObject;
import com.unicom.vac.bean.LockMessageBean;
import com.unicom.vac.util.VACConstants;
import com.unicom.vac.util.VACUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by zhaofrancis on 15/2/11.
 */
@Service
public class UnicomVACService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnicomVACService.class);
    private static final Logger VAC_LOGGER = LoggerFactory.getLogger("vac");
    private static final Logger VAC_ERROR_LOGGER = LoggerFactory.getLogger("vac_error");

    private Socket socket = null;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private boolean isAuthorized = false;
    private static Properties paramsProperties = new Properties();
    private static ConcurrentHashMap<String, LockMessageBean> messageLockMap = new ConcurrentHashMap<String, LockMessageBean>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);
    private static Thread receiveThread = null;
    private static final int REQUEST_MAX_WAIT_SECONDS = 30;
    private static final int REQUEST_TIMEOUT_MILLI_SECONDS = 30 * 60 * 1000;

    static {
        try {
            paramsProperties.load(UnicomVACService.class.getClassLoader().getResourceAsStream("vac.properties"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    public UnicomVACService() {
        this.connect();

        if (!this.bind()) {
            System.exit(0);
        }
    }

    private void connect() {
        LOGGER.info("start connect");
        try {
            if (null == socket || socket.isClosed()) {
                socket = new Socket("127.0.0.1", 8088);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                receiveThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = "";
                        for (;;) {
                            try {
                                content = bufferedReader.readLine();
                                System.out.println("content: " + content);
                            } catch (IOException e) {
                                LOGGER.error("read error: " + e.getMessage(), e);
                            }
                            if (StringUtils.isNotBlank(content)) {
                                VAC_LOGGER.info(content);
                                executorService.submit(new MessageHandler(content));
                            }
                        }
                    }
                });
                receiveThread.start();
            }

            //start thread to check the connection.
            new UnicomVACStatusService(this);
        } catch (Exception e ) {
            LOGGER.error(e.getMessage(), e);
            System.exit(0);
        }
        LOGGER.info("sucess connect");
    }



    private synchronized void sendPDU(String message) throws IOException {
        //check whether connection is alive before send message.
        this.bufferedWriter.write(message);
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
    }

    public boolean bind() {
        LOGGER.info("start bind");
        String commandIDHex = "10000001";
        String sequenceIDHex = UnicomVACService.getSequenceIDHex();
        String timeStampHex = VACUtil.getHexMDHMSDate();

        StringBuilder bodyBuidler = new StringBuilder();
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SOURCE_DEVICE_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SOURCE_DEVICE_ID));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DESTINATION_DEVICE_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DESTINATION_DEVICE_ID));
        //TODO
//        bodyBuidler.append(VACUtil.MD5());
        bodyBuidler.append(timeStampHex);
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.VERSION));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        UnicomVACService.messageLockMap.put(sequenceIDHex, new LockMessageBean(countDownLatch));

        try {
            this.sendPDU(this.buildPDU(bodyBuidler.toString(), commandIDHex, sequenceIDHex));
        } catch (Exception e) {
            UnicomVACService.messageLockMap.remove(sequenceIDHex);
        }

        LockMessageBean lockMessageBean = null;
        try {
            countDownLatch.await(UnicomVACService.REQUEST_MAX_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lockMessageBean = UnicomVACService.messageLockMap.remove(sequenceIDHex);
        }

        String resultCode = UnicomVACService.parseResultCode(lockMessageBean);
        if (StringUtils.isNotBlank(resultCode)) {
            LOGGER.info("bind response: " + lockMessageBean.getResponseContent());

            if ("0".equals(resultCode)) {
                this.isAuthorized = true;
                LOGGER.info("success bind");
                return true;

            } else if ("7".equals(resultCode)) {
                this.unbind();
            }
        }
        LOGGER.info("end bind");
        return false;
    }

    public synchronized void unbind() {
        int time = 3;
        while (time > 0) {
            LOGGER.info("start unbind");
            String commandIDHex = "10000002";
            String sequenceIDHex = UnicomVACService.getSequenceIDHex();

            CountDownLatch countDownLatch = new CountDownLatch(1);
            UnicomVACService.messageLockMap.put(sequenceIDHex, new LockMessageBean(countDownLatch));

            try {
                this.sendPDU(this.buildPDU(null, commandIDHex, sequenceIDHex));
            } catch (Exception e) {
                UnicomVACService.messageLockMap.remove(sequenceIDHex);
                time--;
                continue;
            }

            LockMessageBean lockMessageBean = null;
            try {
                countDownLatch.await(UnicomVACService.REQUEST_MAX_WAIT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                lockMessageBean = UnicomVACService.messageLockMap.remove(sequenceIDHex);
                this.isAuthorized = false;
            }

            boolean unbindResult = false;
            //parse unbind response
            String resultCode = UnicomVACService.parseResultCode(lockMessageBean);
            if (StringUtils.isNotBlank(resultCode) && "0".equals(resultCode)) {
                unbindResult = true;
            }
            if (unbindResult) {
                LOGGER.info("success unbind");
                return;
            } else {
                time--;
                LOGGER.info("unbind fail");
            }
            LOGGER.info("end unbind");
        }
        LOGGER.info("unbind fail max time");
    }

    public boolean sendHandset(int timeoutSeconds) {
        String commandIDHex = "10000003";
        String sequenceIDHex = UnicomVACService.getSequenceIDHex();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        UnicomVACService.messageLockMap.put(sequenceIDHex, new LockMessageBean(countDownLatch));

        try {
            this.sendPDU(this.buildPDU(null, commandIDHex, sequenceIDHex));
        } catch (Exception e) {
            UnicomVACService.messageLockMap.remove(sequenceIDHex);
            return false;
        }

        LockMessageBean lockMessageBean = null;
        try {
            countDownLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lockMessageBean = UnicomVACService.messageLockMap.remove(sequenceIDHex);
        }

        if (null != lockMessageBean && StringUtils.isNotBlank(lockMessageBean.getResponseContent())) {
            return true;
        }
        return false;
    }

    public Map<String, String> consume(JSONObject jsonObject) {
        if (!this.isAuthorized || null == socket) {
            this.reopen();
        }
//TODO check param

        VAC_LOGGER.info(jsonObject.toJSONString());
        //business logic
        String commandIDHex = "10000005";
        String sequenceIDHex = UnicomVACService.getSequenceIDHex();

        StringBuilder bodyBuidler = new StringBuilder();
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SOURCE_DEVICE_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SOURCE_DEVICE_ID));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DESTINATION_DEVICE_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DESTINATION_DEVICE_ID));
        String sequenceNumberHex = VACUtil.formatParam(String.valueOf(new Random().nextInt()), 20);
        bodyBuidler.append(sequenceNumberHex);
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.OA_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.OA_NETWORK_ID));
        bodyBuidler.append(VACUtil.formatParam(jsonObject.getString("userid"), 36));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DA_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.DA_NETWORK_ID));
        bodyBuidler.append(VACUtil.formatParam(jsonObject.getString("userid"), 36));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.FA_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.FA_NETWORK_ID));
        bodyBuidler.append(VACUtil.formatParam(jsonObject.getString("userid"), 36));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SERVICE_ID_TYPE));
        bodyBuidler.append(VACUtil.formatParam(UnicomVACService.paramsProperties.getProperty(VACConstants.SP_ID), 21));
        bodyBuidler.append(VACUtil.formatParam(jsonObject.getString(VACConstants.VAC_CODE), 21));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.PRODUCT_ID));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SERVICE_UPDOWN_TYPE));
        bodyBuidler.append(VACUtil.getHexYMDHMSDate());
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.RESENT_TIMES));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.OPERATION_TYPE));
        bodyBuidler.append(UnicomVACService.paramsProperties.getProperty(VACConstants.SERVICE_TYPE));
        bodyBuidler.append(UnicomVACService.getHexOperationParams(jsonObject.getString(VACConstants.PAY_FEE)));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        UnicomVACService.messageLockMap.put(sequenceIDHex, new LockMessageBean(countDownLatch));

        try {
            String message = this.buildPDU(bodyBuidler.toString(), commandIDHex, sequenceIDHex);
            VAC_LOGGER.info(message);
            this.sendPDU(message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VAC_ERROR_LOGGER.error(jsonObject.toJSONString() + " send error", e);
            UnicomVACService.messageLockMap.remove(sequenceIDHex);
            return null;
        }

        LockMessageBean lockMessageBean = null;
        try {
            countDownLatch.await(UnicomVACService.REQUEST_MAX_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lockMessageBean = UnicomVACService.messageLockMap.remove(sequenceIDHex);
        }

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(VACConstants.RESULT_CODE, UnicomVACService.parseResultCode(lockMessageBean));
        resultMap.put(VACConstants.VAC_CODE, jsonObject.getString(VACConstants.VAC_CODE));
        resultMap.put(VACConstants.USER_ID, jsonObject.getString(VACConstants.USER_ID));
        resultMap.put(VACConstants.TIME, String.valueOf(System.currentTimeMillis()));
        return resultMap;
    }

    public synchronized void reopen() {
        if (this.isAuthorized && null != socket) {
            return;
        }
        int time = 3;
        while (time > 0) {
            LOGGER.info("start reopen");
            try {
                this.isAuthorized = false;
                this.clear();

                this.connect();
                this.bind();
                LOGGER.info("success reopen");
                return;
            } catch (Exception e) {
                time--;
                LOGGER.error("reopen fail");
            }
            LOGGER.info("end reopen");
        }
        LOGGER.error("reopen fail max time");
    }

    private void clear() {
        try {
            this.isAuthorized = false;

            if (null != this.bufferedReader) {
                bufferedReader.close();
            }

            if (null != this.bufferedWriter) {
                bufferedWriter.close();
            }
            if (null != socket) {
                socket.close();
            }
            if (null != receiveThread) {
                receiveThread.interrupt();
            }
            if (null != UnicomVACService.messageLockMap) {
                UnicomVACService.messageLockMap = null;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            bufferedReader = null;
            bufferedWriter = null;
            socket = null;
            receiveThread = null;
            UnicomVACService.messageLockMap = new ConcurrentHashMap<String, LockMessageBean>(10000);
        }
    }

    private void breakConnect() {

    }

    private String buildPDU(String body, String commandIDHex, String sequenceIDHex) {
        StringBuilder pduBuilder = new StringBuilder();
        if (null != body) {
            //header's length is 12 and body is hex(16) string.
            pduBuilder.append(VACUtil.formatParam(12 + body.length() / 2, 4));
        } else {
            //header's length is 12 and body is hex(16) string.
            pduBuilder.append(VACUtil.formatParam(12, 4));
        }
        pduBuilder.append(commandIDHex);
        pduBuilder.append(sequenceIDHex);
        pduBuilder.append(body);
        return pduBuilder.toString();
    }

    private class MessageHandler implements Callable {
        private String content;

        private MessageHandler(String content) {
            this.content = content;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("start handle");
            String sequenceIDHex = this.content.substring(16, 24);
            if (StringUtils.isBlank(sequenceIDHex)) {
                return null;
            }

            LockMessageBean lockMessageBean = UnicomVACService.messageLockMap.get(sequenceIDHex);
            if (null != lockMessageBean && null != lockMessageBean.getCountDownLatch()) {
                lockMessageBean.setResponseContent(this.content);
                lockMessageBean.getCountDownLatch().countDown();
            }
            System.out.println("finish handle");

            return null;
        }
    }

    private static String getSequenceIDHex() {
        String sequenceIDHex = VACUtil.getHexSequenceId();
        while (UnicomVACService.messageLockMap.containsKey(sequenceIDHex)) {
            sequenceIDHex = VACUtil.getHexSequenceId();
        }
        return sequenceIDHex;
    }

    private static String parseResultCode(LockMessageBean lockMessageBean) {
        if (null == lockMessageBean || StringUtils.isBlank(lockMessageBean.getResponseContent())) {
            return null;
        }
        String resultCodeHex = lockMessageBean.getResponseContent().substring(24, 32);
        return String.valueOf(Integer.parseInt(resultCodeHex, 16));
    }

    private static String getHexOperationParams(String fee) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("15100000");
        if (StringUtils.isNotBlank(fee)) {
            stringBuilder.append("1202000132");
            stringBuilder.append("1508");
            stringBuilder.append(fee.length());
            stringBuilder.append(VACUtil.formatParam(fee, fee.length()));
        }
        stringBuilder.append("15110000");
        stringBuilder.append("15090005" + VACUtil.formatParam(UnicomVACService.paramsProperties.getProperty(VACConstants.SP_ID), 5));
        return stringBuilder.toString();
    }

    public static boolean isBusy() {
        return UnicomVACService.messageLockMap.size() > 0;
    }

    public static void cleanMessageLockMap() {
        for (String sequenceIDHex : UnicomVACService.messageLockMap.keySet()) {
            try {
                LockMessageBean lockMessageBean = UnicomVACService.messageLockMap.get(sequenceIDHex);
                if (System.currentTimeMillis() - lockMessageBean.getCreateTime() > UnicomVACService.REQUEST_TIMEOUT_MILLI_SECONDS) {
                    UnicomVACService.messageLockMap.remove(sequenceIDHex);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

}
