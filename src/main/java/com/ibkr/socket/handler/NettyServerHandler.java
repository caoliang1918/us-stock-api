package com.ibkr.socket.handler;

import com.alibaba.fastjson.JSON;
import com.ibkr.entity.MessageProtocol;
import com.ibkr.socket.server.ChannelRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by caoliang on 2018/7/18
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);




    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive");
        NioSocketChannel nioSocketChannel = (NioSocketChannel) ctx.channel();
        ChannelRepository.remove(nioSocketChannel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String messgae = JSON.toJSONString(msg);
        logger.info("receive message : {}", messgae);
        super.channelRead(ctx, msg);
        if (msg instanceof MessageProtocol) {
            //todo
        }
    }

    /**
     * 心跳机制  用户事件触发
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                logger.warn("60秒没有收到客户端信息,关闭连接！");
                //向客户端发送心跳消息
                MessageProtocol messageProtocol = new MessageProtocol();
                messageProtocol.setId(1L);
                messageProtocol.setStation("1001");
                messageProtocol.setBody("10秒没有收到客户端信息,关闭连接");
                messageProtocol.setCts(new Date());
                ByteBuf byteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(messageProtocol.toString(), CharsetUtil.UTF_8));
                ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("客户端连接  Netty 出错...");
        cause.printStackTrace();
        ChannelRepository.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("register success");
        ctx.fireChannelRegistered();
        NioSocketChannel channel = (NioSocketChannel) ctx.channel();
        ChannelRepository.put("ACS", channel);
    }
}
