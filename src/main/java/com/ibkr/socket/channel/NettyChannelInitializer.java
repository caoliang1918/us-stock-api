package com.ibkr.socket.channel;

import com.ibkr.socket.decoder.MessageDecoder;
import com.ibkr.socket.handler.NettyServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by caoliang on 2018/7/18
 * <p>
 * 服务端
 */
public class NettyChannelInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {

        channel.pipeline().addLast("timeout", new IdleStateHandler(10, 0, 0))
                .addLast(new MessageDecoder())
                .addLast(new NettyServerHandler());

    }
}
