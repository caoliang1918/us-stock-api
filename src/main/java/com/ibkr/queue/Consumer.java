package com.ibkr.queue;

import com.alibaba.fastjson.JSON;
import com.ibkr.entity.MessageQueue;
import com.ibkr.util.Levenshtein;
import com.tigerbrokers.stock.openapi.client.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by caoliang on 2019/1/14
 */

public class Consumer implements Runnable {
    private Logger logger = LoggerFactory.getLogger(Consumer.class);

    /**
     * 延时队列 ,消费者从其中获取消息进行消费
     */
    public QueueService queueService;

    /**
     * 发送http数据
     */
    private RestTemplate restTemplate = new RestTemplate();

    private String wxAddress;


    public Consumer(QueueService queueService, String wxAddress) {
        this.queueService = queueService;
        this.wxAddress = wxAddress;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MessageQueue message = queueService.poll();
                if (message == null) {
                    return;
                }
                logger.info("queue poll : {}", message.toString());
                if (message.getOption().equals("option")) {
                    restTemplate.postForEntity(wxAddress + "sendOption", message, String.class);
                } else {
                    if (checkContent(message)) {
                        logger.info("send message:{}", message.toString());
                        restTemplate.postForEntity(wxAddress + "sendMessage", message, String.class);
                    }
                }
            } catch (Exception e) {
                logger.error("{}", e);
            }
        }
    }


    private boolean checkContent(MessageQueue first) {
        /**
         * 判断相似度
         */
        Levenshtein levenshtein = new Levenshtein();
        Iterator<MessageQueue> iterable = queueService.iterator();
        while (iterable.hasNext()) {
            MessageQueue messageQueue = iterable.next();
            /**
             * 文本相似度
             */
            if (levenshtein.getSimilarityRatio(messageQueue.getContent(), first.getContent()) > 0.6F || messageQueue.getId().equals(first.getId())) {
                return false;
            }
        }
        return true;
    }
}
