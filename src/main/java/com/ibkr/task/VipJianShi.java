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

@Component
public class VipJianShi {
    private Logger logger = LoggerFactory.getLogger(VipJianShi.class);

    private final static String BASE_URL = "https://vip.jianshiapp.com/live/us-stock";

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

        flagList.forEach(s -> {
            if (content.contains(s)) {
                String href = element.getElementsByClass("content").attr("href");
                String id = href.replace("/livenews/", "");
                MessageQueue messageQueue = new MessageQueue();
                messageQueue.setId(Long.parseLong(id));
                messageQueue.setOption("create");
                messageQueue.setDate(new Date());
                messageQueue.setContent(content);
                messageQueue.setChannel("见闻");
                queueService.add(messageQueue);
                return;
            }
        });
    }

}
