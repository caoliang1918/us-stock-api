package com.ibkr.task;

import com.ibkr.entity.MessageQueue;
import com.ibkr.queue.QueueService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Create by caoliang on 2020/10/12
 */
@Component
public class CaiZhiTask {
    private Logger logger = LoggerFactory.getLogger(CaiZhiTask.class);


    private String url = "https://www.zhitongcaijing.com/immediately.html?type=usstock";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QueueService queueService;


    @Scheduled(cron = "0/10 * * * * ?")
    public void init() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity == null || StringUtils.isBlank(responseEntity.getBody())) {
            return;
        }
        Document document = Jsoup.parse(responseEntity.getBody());

        Element element = document.getElementsByClass("allday-box").first();

        Element dl = element.child(1);
        logger.debug("智通财经:{} , {}" , dl.childNodeSize()  , dl.child(0));
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setChannel("智通财经");
        messageQueue.setOption("create");
        messageQueue.setDate(new Date());
        messageQueue.setContent(dl.children().get(0).text());
        messageQueue.setId(Long.valueOf(messageQueue.getContent().hashCode()));
        queueService.add(messageQueue);
    }
}
