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

/**
 * Created by caoliang on 2020-01-01
 */

@Component
public class JinShi {
    private Logger logger = LoggerFactory.getLogger(JinShi.class);

    private final static String BASE_URL = "https://www.jin10.com/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QueueService queueService;


    @Scheduled(cron = "0/10 * * * * ?")
    public void runTask() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL, String.class);
        if (responseEntity == null || StringUtils.isBlank(responseEntity.getBody())) {
            return;
        }
        Document document = Jsoup.parse(responseEntity.getBody());
        //快讯板块
        Element jinFlash = document.getElementById("J_flashList");
        Element element = jinFlash.child(0);
        logger.debug("element: \n{}", element);

        String content = element.child(1).html();
        if (content.contains("图示") || content.contains("金十网站") || content.contains("金十早知道")) {
            return;
        }

        Element hrefEle = element.child(0).getElementsByClass("jin-flash_icon").get(0);
        String id = hrefEle.children().get(0).attr("href").substring(25, 41);
        if (content.contains("高盛") || content.contains("摩根") || content.contains("行情") || content.contains("开盘") || content.contains("收盘") || content.contains("纳斯达克") || content.contains("标普")) {
            MessageQueue messageQueue = new MessageQueue();
            messageQueue.setId(Long.parseLong(id));
            messageQueue.setOption("create");
            messageQueue.setDate(new Date());
            content = content.replace("<b>", "").replace("</b>", "");
            content = content.replace("<br>", "").replace("</br>", "");
            messageQueue.setContent(content.replace("<h4>", "").replace("</h4>", ""));
            queueService.add(messageQueue);
            logger.info("id:{} , content:{}", messageQueue.getId(), messageQueue.getContent());
        }
    }
}
