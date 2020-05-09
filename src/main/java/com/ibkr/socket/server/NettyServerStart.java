package com.ibkr.socket.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zhongweixian.listener.ConnectionListener;
import org.zhongweixian.server.tcp.NettyServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by caoliang on 2018/7/18
 */
@Component
public class NettyServerStart {
    private Logger logger = LoggerFactory.getLogger(NettyServerStart.class);


    @Value("${tcp.port}")
    private int port;

    private NettyServer nettyServer;


    @PostConstruct
    public void start() {
        nettyServer = new NettyServer(port, new ConnectionListener() {
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
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (jsonObject != null && "ping".equals(jsonObject.getString("cmd"))) {
                    channel.writeAndFlush("{\"cmd\":\"pong\"," + System.currentTimeMillis() + ":}");
                }
            }

            @Override
            public void onMessage(Channel channel, ByteBuf byteBuf) throws Exception {

            }

            @Override
            public void connect(Channel channel) throws Exception {

            }
        });
        nettyServer.start();
    }

    @PreDestroy
    public void destory() {
        if (nettyServer != null) {
            logger.info("netty stop");
        }
    }


    public void sendMesage(String channel, String messageReq) {

    }
}
