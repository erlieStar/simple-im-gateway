package com.javashitang.wshandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

@Component
public class IdleUserEventHandler extends SimpleUserEventChannelHandler<IdleStateEvent> {

    @Override
    protected void eventReceived(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        switch (evt.state()) {
            case READER_IDLE:
                if (evt.isFirst()) {
                    // 第一次超时发送ping，第二次直接关闭通道
                    ctx.channel().writeAndFlush(new PingWebSocketFrame());
                } else {
                    ctx.close();
                }
                break;
            case WRITER_IDLE:
                break;
            case ALL_IDLE:
                break;
            default:
                break;
        }
    }
}
