package com.unicom.sms.queue.local;

import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.task.MessageTask;
import com.unicom.sms.util.SMSContants;

import javax.xml.ws.Service;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public class LocalMessageQueue {
    private static final LinkedBlockingQueue<MessageTask> LINKED_BLOCKING_QUEUE = new LinkedBlockingQueue<MessageTask>();

    public static void put(MessageTask messageTask) throws ServiceException {
        if (messageTask == null) {
            return;
        }

        if (!LINKED_BLOCKING_QUEUE.offer(messageTask)) {
            throw new ServiceException(SMSContants.PUT_QUEUE_ERROR);
        }
    }

    public static MessageTask take() throws InterruptedException {
        return LINKED_BLOCKING_QUEUE.take();
    }
}
