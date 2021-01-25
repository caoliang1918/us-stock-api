package com.ibkr.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.MessageQueue;
import com.ibkr.queue.QueueService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * 华尔街见闻
 */
@Component
public class VipJianShi {
    private Logger logger = LoggerFactory.getLogger(VipJianShi.class);

    private final static String BASE_URL = "https://api.wallstcn.com/apiv1/content/lives?channel=global-channel&accept=live%2Cvip-live&limit=20&cursor=";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QueueService queueService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void task() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL, String.class);
        if (responseEntity == null || StringUtils.isBlank(responseEntity.getBody())) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        if (jsonObject == null) {
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");

        String next_cursor = data.getString("next_cursor");
        JSONArray items = data.getJSONArray("items");
        if (items == null || items.size() == 0) {
            return;
        }
        JSONObject element = items.getJSONObject(0);

        logger.debug("element: \n{}", element);

        String content = element.getString("content_text");
        Long id = element.getLong("id");
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setId(id);
        messageQueue.setOption("create");
        messageQueue.setDate(new Date());
        messageQueue.setContent(content);
        messageQueue.setChannel("见闻");
        queueService.add(messageQueue);
    }

}
