package com.ibkr.queue;

import com.ibkr.entity.MessageQueue;
import com.ibkr.socket.client.SimpleEchoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by caoliang on 2019/1/14
 */
@Service
public class QueueServiceImpl implements QueueService {

    private Map<Long, MessageQueue> queueMap = new HashMap<>();

    public DelayQueue<MessageQueue> queue = new DelayQueue<MessageQueue>();


    @Autowired
    private SimpleEchoClient simpleEchoClient;

    @Override
    public void add(MessageQueue messageQueue) {
        MessageQueue exist = queueMap.get(messageQueue.getId());
        if (exist != null) {
            queue.remove(exist);
            queueMap.remove(messageQueue.getId());
        }
        queue.add(messageQueue);
        queueMap.put(messageQueue.getId() , messageQueue);
    }

    @Override
    public MessageQueue poll() {
        MessageQueue messageQueue = queue.poll();
        if (messageQueue == null) {
            return null;
        }
        queueMap.remove(messageQueue.getId());
        return messageQueue;
    }

    @Override
    public Iterator<MessageQueue> iterator() {
        return queueMap.values().iterator();
    }

    @Override
    public void wsClose() {
        try {
            simpleEchoClient.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
