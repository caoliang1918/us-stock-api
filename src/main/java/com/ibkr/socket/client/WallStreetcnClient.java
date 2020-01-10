package com.ibkr.socket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.MessageQueue;
import com.ibkr.queue.Consumer;
import com.ibkr.queue.QueueService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zhongweixian.client.websocket.WsClient;
import org.zhongweixian.listener.ConnectionListener;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by caoliang on 2019-12-03
 * <p>
 * 获取华尔街见闻websocket消息
 */
@Component
public class WallStreetcnClient {
    private Logger logger = LoggerFactory.getLogger(WallStreetcnClient.class);

    /**
     * 监听地址
     */
    private final static String WS_URL = "wss://realtime-prod.wallstreetcn.com/ws";
    /**
     * 登录授权
     */
    private String payload = "{\"command\":\"ENTER_CHANNEL\",\"data\":{\"chann_name\":\"live\",\"cursor\":\"3484360\"}}";

    private ScheduledExecutorService threadPoolExecutor = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("wall-pool-%d").daemon(true).build());


    @Autowired
    private QueueService queueService;

    @Value("${wx.address}")
    private String wxAddress;

    @PostConstruct
    public void init() {
        try {
            WsClient wsClient = new WsClient(WS_URL, payload, new ConnectionListener() {
                @Override
                public void onClose(Channel channel, int i, String s) {
                    logger.warn("onClose:{} , code:{}", s, i);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onFail(int i, String s) {

                }

                @Override
                public void onMessage(Channel channel, String s) throws Exception {
                    JSONObject jsonObject = JSON.parseObject(s);
                    String data = jsonObject.getString("data");
                    logger.info("receive socket message :{} ", data);
                    if (StringUtils.isBlank(data) || data.length() < 5) {
                        return;
                    }

                    JSONObject text = JSONObject.parseObject(data);
                    String contentText = text.getString("content_text");
                    try {
                        MessageQueue messageQueue = new MessageQueue();
                        messageQueue.setId(text.getLong("id"));
                        messageQueue.setOption(text.getString("op_name"));
                        messageQueue.setDate(new Date());
                        messageQueue.setContent(contentText);
                        if (data.contains("us-stock-channel")) {
                            queueService.add(messageQueue);
                        }
                    } catch (Exception e) {
                        logger.error("{}", e);
                    }
                }

                @Override
                public void onMessage(Channel channel, ByteBuf byteBuf) throws Exception {

                }

                @Override
                public void connect(Channel channel) throws Exception {

                }
            });
            wsClient.setAutoConnect(false);
            threadPoolExecutor.execute(wsClient);
        } catch (Exception e) {
            logger.error("{}", e);
        }
        threadPoolExecutor.execute(new Consumer(queueService, wxAddress));
    }

}
