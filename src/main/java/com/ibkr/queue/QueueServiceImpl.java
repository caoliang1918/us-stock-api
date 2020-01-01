package com.ibkr.queue;

import com.ibkr.entity.MessageQueue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.DelayQueue;

/**
 * Created by caoliang on 2019/1/14
 */
@Service
public class QueueServiceImpl implements QueueService {

    private Map<Long, MessageQueue> queueMap = new HashMap<>();

    public DelayQueue<MessageQueue> queue = new DelayQueue<MessageQueue>();


    @Override
    public void add(MessageQueue messageQueue) {
        MessageQueue exist = queueMap.get(messageQueue.getId());
        if (exist != null) {
            queue.remove(exist);
            queueMap.remove(messageQueue.getId());
        }
        queue.add(messageQueue);
        queueMap.put(messageQueue.getId(), messageQueue);
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
}
