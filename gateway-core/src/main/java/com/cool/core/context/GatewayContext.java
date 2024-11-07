package com.cool.core.context;

import com.cool.core.request.GatewayRequest;
import com.cool.core.response.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;

/**
 * 网关核心上下文类
 */
public class GatewayContext extends BaseContext{

    public GatewayRequest request;

    public GatewayResponse response;

    public Rule rule;

    /**
     * 构造函数
     *
     * @param protocol
     * @param nettyCtx
     * @param keepAlive
     */
    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        super(protocol, nettyCtx, keepAlive);
    }
}
