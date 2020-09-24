package com.ibkr.web;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by caoliang on 2020/9/24
 */
@RestController
@RequestMapping("index")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    private String url = "http://api.d1-bus.com/wechat/share/getCodeShareCoupon?mobile=186";

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("http-executor-pool"));


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("sendSms")
    public String sendHttp() {

        while (true) {
            executor.execute(() -> {
                ResponseEntity<String> responseBody = restTemplate.getForEntity(url + RandomStringUtils.randomNumeric(8), String.class);
                logger.info("{}", responseBody.getBody());
            });

        }

    }
}
