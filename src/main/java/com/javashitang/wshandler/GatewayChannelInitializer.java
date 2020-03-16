package com.javashitang.wshandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Value("${im.server.websocket.heartbeat.read.timeout}")
    private Integer readerIdleTime;
    @Value("${im.server.websocket.heartbeat.write.timeout}")
    private Integer writerIdleTime;
    @Value("${im.server.websocket.heartbeat.all.timeout}")
    private Integer allIdleTime;

    @Autowired
    private IdleUserEventHandler idleUserEventHandler;
    @Autowired
    private WsHandShakeEventHandler wsHandShakeEventHandler;
    // 解码器
    @Autowired
    private ChatMessageDecoder chatMessageDecoder;
    // 编码器
    @Autowired
    private ChatMessageEncoder chatMessageEncoder;
    // 异步线程组
    @Autowired
    private DefaultEventLoopGroup asyncEventLoopGroup;
    @Autowired
    private AsyncWsInboundHandler asyncWsInboundHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 多长时间没有读到客户端的数据，有可能发生连接假死
        pipeline.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime));
        pipeline.addLast(idleUserEventHandler);
        pipeline.addLast(new HttpServerCodec());
        // 聚合http消息
        pipeline.addLast(new HttpObjectAggregator(65535));
        // tcp流量控制
        pipeline.addLast(new FlowControlHandler());
        // 握手事件监听器
        pipeline.addLast(wsHandShakeEventHandler);
        pipeline.addLast(chatMessageDecoder);
        pipeline.addLast(chatMessageEncoder);
        pipeline.addLast(asyncEventLoopGroup, asyncWsInboundHandler);
    }
}
