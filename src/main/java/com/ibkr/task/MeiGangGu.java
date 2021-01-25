package com.ibkr.task;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zhongweixian.client.websocket.WsClient;
import org.zhongweixian.listener.ConnectionListener;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by caoliang on 2020/12/31
 */
@Component
public class MeiGangGu {

    private Logger logger = LoggerFactory.getLogger(MeiGangGu.class);


    private ScheduledExecutorService threadPoolExecutor = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("stock-pool-%d").daemon(true).build());

//    @PostConstruct
    public void init() throws Exception {
        WsClient wsClient = new WsClient("wss://sscphcdpfn.jin10.com:8083/socket.io/?EIO=3&transport=websocket&sid=i8Ob9d4rjwbw-PciNx8k", "", new ConnectionListener() {
            @Override
            public void onClose(Channel channel, int closeCode, String reason) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFail(int status, String reason) {

            }

            @Override
            public void onMessage(Channel channel, String text) throws Exception {
                logger.info("receive:{}", text);
            }

            @Override
            public void onMessage(Channel channel, ByteBuf byteBuf) throws Exception {

            }

            @Override
            public void connect(Channel channel) throws Exception {

            }
        });

        wsClient.setAutoReConnect(false);
        threadPoolExecutor.execute(wsClient);
    }

    public static void main(String[] args) throws Exception {
        new MeiGangGu().init();
    }
}
