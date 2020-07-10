package com.ibkr.task;

import com.ibkr.entity.MessageQueue;
import com.ibkr.queue.QueueService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class VipJianShi {
    private Logger logger = LoggerFactory.getLogger(VipJianShi.class);

    private final static String BASE_URL = "https://vip.jianshiapp.com/live/us-stock";

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
        Document document = Jsoup.parse(responseEntity.getBody());
        //快讯美股
        Elements groupByDay = document.getElementsByClass("group-by-day");
        if (groupByDay == null || groupByDay.size() < 1) {
            return;
        }
        Element element = groupByDay.get(0).child(1);
        logger.debug("element: \n{}", element);

        String content = element.getElementsByClass("content").text();
        String href = element.getElementsByClass("content").attr("href");
        String id = href.replace("/livenews/" ,"");
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setId(Long.parseLong(id));
        messageQueue.setOption("create");
        messageQueue.setDate(new Date());
        messageQueue.setContent(content);
        messageQueue.setChannel("见闻");
        queueService.add(messageQueue);
    }

}
