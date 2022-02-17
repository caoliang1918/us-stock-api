package com.ibkr.queue;

import com.ibkr.entity.MessageQueue;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by caoliang on 2019/1/14
 */
@Service
public class QueueServiceImpl implements QueueService {
    private Logger logger = LoggerFactory.getLogger(QueueServiceImpl.class);


    @Value("${wx.address}")
    private String wxAddress;

    private Map<Long, MessageQueue> queueMap = new HashMap<>();
    private Set<Long> sendMessage = new HashSet<>();

    public DelayQueue<MessageQueue> queue = new DelayQueue<MessageQueue>();

    private ScheduledExecutorService threadPoolExecutor = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("wall-pool-%d").daemon(true).build());


    @PostConstruct
    public void init(){
        threadPoolExecutor.execute(new Consumer(this, wxAddress));

    }

    @Override
    public void add(MessageQueue messageQueue) {
        if (sendMessage.contains(messageQueue.getId())) {
            logger.debug("message:{} has sended", messageQueue.getId());
            return;
        }
        MessageQueue exist = queueMap.get(messageQueue.getId());
        if (exist != null) {
            queue.remove(exist);
            exist.setContent(messageQueue.getContent());
            queue.put(exist);
            return;
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
        sendMessage.add(messageQueue.getId());
        queueMap.remove(messageQueue.getId());
        return messageQueue;
    }

    @Override
    public Iterator<MessageQueue> iterator() {
        return queueMap.values().iterator();
    }
}
