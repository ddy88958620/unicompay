package com.unicom.sms.queue.handler;

import com.unicom.sms.queue.task.MessageTask;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public abstract class MessageHandler {
    public void before(MessageTask messageTask){
        //TODO
    }

    public void after(MessageTask messageTask) {
        //TODO
    }

    public void execute(MessageTask messageTask) {
        this.before(messageTask);
        this.handle(messageTask);
        this.after(messageTask);
    }

    public abstract void handle(MessageTask messageTask);
}
