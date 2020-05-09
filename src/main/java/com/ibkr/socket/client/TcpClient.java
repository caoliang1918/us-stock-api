package com.ibkr.socket.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zhongweixian.client.AuthorizationToken;
import org.zhongweixian.client.tcp.NettyClient;
import org.zhongweixian.listener.ConnectionListener;

import javax.annotation.PostConstruct;

/**
 * Created by caoliang on 2020-02-24
 */
public class TcpClient {
    private Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private NettyClient nettyClient;

    @PostConstruct
    private void start() {
        String payload = "{\"cmd\":\"cmdconnect\",\"asStation\":61,\"senderUri\":\"http://192.168.183.146:8083/acd\"}";
        AuthorizationToken authorization = new AuthorizationToken();
        authorization.setPayload(payload);
        nettyClient = new NettyClient("192.168.183.146", 2525, authorization, new ConnectionListener() {
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
                logger.info("{}", s);
            }

            @Override
            public void onMessage(Channel channel, ByteBuf byteBuf) throws Exception {
                logger.info("{}", byteBuf);
            }

            @Override
            public void connect(Channel channel) throws Exception {
                logger.info("{}", channel);
                channel.writeAndFlush(payload);
            }
        });
    }

    public void sendMessage(String message) {
        if (nettyClient == null) {
            return;
        }
        nettyClient.sendMessage(message);
    }
}
