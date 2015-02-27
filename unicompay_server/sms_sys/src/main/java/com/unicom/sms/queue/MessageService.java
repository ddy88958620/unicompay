package com.unicom.sms.queue;

import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.local.LocalMessageQueue;
import com.unicom.sms.queue.task.MessageTask;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaofrancis on 15/1/23.
 */
@Service
public class MessageService implements Runnable {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(700);

    public MessageService() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        for(;;) {
            try {
                MessageTask messageTask = LocalMessageQueue.take();
                if (null == messageTask) {
                    continue;
                }
                EXECUTOR_SERVICE.submit(new MessageThread(messageTask));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupted();
            }
        }
    }

    private class MessageThread implements Runnable {
        private MessageTask messageTask;

        public MessageThread(MessageTask messageTask) {
            this.messageTask = messageTask;
        }

        @Override
        public void run() {
            messageTask.execute();
        }
    }
}
