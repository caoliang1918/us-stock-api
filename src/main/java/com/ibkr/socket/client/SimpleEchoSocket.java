package com.ibkr.socket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.MessageQueue;
import com.ibkr.queue.QueueService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.*;

public class SimpleEchoSocket implements WebSocketListener {
    private Logger logger = LoggerFactory.getLogger(SimpleEchoSocket.class);

    private final CountDownLatch closeLatch;

    private final static long TIME_OUT = 2L;
    @SuppressWarnings("unused")
    private Session session;

    private QueueService queueService;
    private String wxAddress;
    /**
     * 用户登录名
     */
    private String host;
    private String startMessage;

    private volatile boolean heartsBeats = false;


    public SimpleEchoSocket(String host, String startMessage, QueueService queueService, String wxAddress) {
        this.closeLatch = new CountDownLatch(1);
        this.host = host;
        this.startMessage = startMessage;
        this.queueService = queueService;
        this.wxAddress = wxAddress;
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }


    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {
        logger.info("message {} , {} ,{}", new String(bytes), i, i1);
    }

    @Override
    public void onWebSocketText(String s) {
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
    public void onWebSocketClose(int i, String s) {
        logger.info("Connection closed statusCode:{} ,reason:{} ", i, s);
        this.session = null;
        this.closeLatch.countDown();
    }

    @Override
    public void onWebSocketConnect(Session session) {
        logger.info("Got connect: {}", session);
        this.session = session;
        Future<Void> fut;
        try {
            fut = session.getRemote().sendStringByFuture(startMessage);
            fut.get(TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketError(Throwable throwable) {
        logger.error(throwable.getMessage());
    }


    public Session getSession() {
        return session;
    }

    public void sendMessage(String message) {
        if (session == null || !session.isOpen()) {
            return;
        }
        session.getRemote().sendStringByFuture(message);
    }


}
