package com.ibkr.socket.server;

import com.ibkr.socket.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Created by caoliang on 2018/7/18
 */
@Component
public class NettyServerStart {
    private Logger logger = LoggerFactory.getLogger(NettyServerStart.class);


    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup(10);

    @Value("${tcp.port}")
    private int port;


    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(60, 0, 0))
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 4))
                                .addLast(new NettyServerHandler());

                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        if (channelFuture.isSuccess()) {
            logger.info("netty server start , port :{}", port);
        }


    }

    @PreDestroy
    public void destory() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        logger.info("netty stop");
    }


    public void sendMesage(String channel, String messageReq) {
        NioSocketChannel nioSocketChannel = ChannelRepository.getChannel(channel);
        if (nioSocketChannel == null || !nioSocketChannel.isOpen()) {
            logger.error("客户端 : {} 不存在或者已关闭!", channel);
            return;
        }
        ChannelFuture future = nioSocketChannel.writeAndFlush(Unpooled.copiedBuffer(messageReq, CharsetUtil.UTF_8));
        future.addListener((ChannelFutureListener) channelFuture ->
                logger.info("服务端发消息成功:{}", messageReq));
    }
}
