package com.ibkr.queue;

import com.alibaba.fastjson.JSON;
import com.ibkr.entity.MessageQueue;
import com.tigerbrokers.stock.openapi.client.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

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
    private RestTemplate restTemplate;

    private String wxAddress;


    public Consumer(QueueService queueService, RestTemplate restTemplate, String wxAddress) {
        this.queueService = queueService;
        this.restTemplate = restTemplate;
        this.wxAddress = wxAddress;
    }

    @Override
    public void run() {

        while (true) {
            try {
                MessageQueue message = queueService.poll();
                if (message != null) {
                    logger.info("{}", message.toString());
                    if (message.getOption().equals("option")){
                        restTemplate.postForEntity(wxAddress+"sendOption", message, String.class);
                    }else {
                        restTemplate.postForEntity(wxAddress+"sendMessage", message, String.class);
                    }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
