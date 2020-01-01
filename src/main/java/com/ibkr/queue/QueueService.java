package com.ibkr.queue;

import com.ibkr.entity.MessageQueue;

import java.util.Iterator;

/**
 * Created by caoliang on 2019/1/14
 */
public interface QueueService {

    /**
     * 添加元素
     *
     * @param messageQueue
     */
    void add(MessageQueue messageQueue);


    /**
     * 弹出元素
     */
    MessageQueue poll();

    Iterator<MessageQueue> iterator();


}
