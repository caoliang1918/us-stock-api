package com.ibkr.socket.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zhongweixian.client.tcp.NettyClient;
import org.zhongweixian.listener.ConnectionListener;

import javax.annotation.PostConstruct;

/**
 * Created by caoliang on 2020-02-24
 */
@Component
public class TcpClient {
    private Logger logger = LoggerFactory.getLogger(TcpClient.class);


    @PostConstruct
    public void start() {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9199, new ConnectionListener() {
            @Override
            public void onClose(Channel channel, int i, String s) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFail(int i, String s) {

            }

            @Override
            public void onMessage(Channel channel, String s) throws Exception {

            }

            @Override
            public void onMessage(Channel channel, ByteBuf byteBuf) throws Exception {

            }

            @Override
            public void connect(Channel channel) throws Exception {
                logger.info("{}", channel);
            }
        });
    }
}
