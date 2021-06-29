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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<String> flagList = new ArrayList<>();

    {
        flagList.add("摩根");
        flagList.add("高盛");
        flagList.add("瑞银");
        flagList.add("评级");
        flagList.add("行情");
        flagList.add("开盘");
        flagList.add("收盘");
        flagList.add("纳斯达克");
        flagList.add("标普");
        flagList.add("指数");
        flagList.add("美股");
        flagList.add("期货");
        flagList.add("前值");
        flagList.add("IPO");
        flagList.add("上涨");

    }


    @Scheduled(cron = "0/10 * * * * ?")
    public void runTask() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL, String.class);
        if (responseEntity == null || StringUtils.isBlank(responseEntity.getBody())) {
            return;
        }
        Document document = Jsoup.parse(responseEntity.getBody());
        //快讯板块
        Element jinFlash = document.getElementById("jin_flash_list");
        Element element = jinFlash.child(1);
        logger.debug("element: \n{}", element);

        final String content = element.child(1).text();
        if (content.contains("图示") || content.contains("金十")) {
            return;
        }

        String id = element.attr("id");
        flagList.forEach(s -> {
            if (content.contains(s)) {
                String text = content;
                MessageQueue messageQueue = new MessageQueue();
                messageQueue.setId(Long.parseLong(id.substring(5, 23)));
                messageQueue.setOption("create");
                messageQueue.setDate(new Date());
                messageQueue.setChannel("金十");
                text = text.replace("<b>", "").replace("</b>", "");
                text = text.replace("<br>", "\n").replace("</br>", "");
                messageQueue.setContent(text.replace("<h4>", "").replace("</h4>", ""));
                queueService.add(messageQueue);
                logger.info("id:{} , content:{}", messageQueue.getId(), messageQueue.getContent());
                return;
            }
        });
    }
}
