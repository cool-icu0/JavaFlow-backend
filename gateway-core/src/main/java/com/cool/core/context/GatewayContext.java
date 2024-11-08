package com.cool.core.context;

import io.netty.channel.ChannelHandlerContext;
import com.cool.common.rule.Rule;
import com.cool.common.utils.AssertUtil;
import com.cool.core.request.GatewayRequest;
import com.cool.core.response.GatewayResponse;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;


/**
 * 网关核心上下文类
 */
public class GatewayContext extends BaseContext {

    public final GatewayRequest request;

    public GatewayResponse response;

    public final Rule rule;

    /**
     * 构造函数
     *
     * @param protocol
     * @param nettyCtx
     * @param keepAlive
     */
    public GatewayContext(String protocol,
                          ChannelHandlerContext nettyCtx,
                          boolean keepAlive,
                          GatewayRequest request,
                          Rule rule) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.rule = rule;
    }

    /**
     * 建造者类
     */
    public static class Builder {

        private String protocol;

        private ChannelHandlerContext nettyCtx;

        private GatewayRequest request;

        private Rule rule;

        private boolean keepAlive;

        public Builder() {
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setNettyCtx(ChannelHandlerContext nettyCtx) {
            this.nettyCtx = nettyCtx;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setRequest(GatewayRequest request) {
            this.request = request;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public GatewayContext build() {
            AssertUtil.notNull(protocol, "protocol不能为空");
            AssertUtil.notNull(nettyCtx, "nettyCtx不能为空");
            AssertUtil.notNull(request, "request不能为空");
            AssertUtil.notNull(rule, "rule不能为空");
            return new GatewayContext(protocol, nettyCtx, keepAlive, request, rule);
        }
    }

    /**
     * 获取必要的上下文参数，如果没有则抛出IllegalArgumentException
     * @param key
     * @return
     * @param <T>
     */
    public <T> T getRequiredAttribute(String key) {
        T value = getAttribute(key);
        AssertUtil.notNull(value, "缺乏必要的上下文参数");
        return value;
    }

    /**
     * 获取指定key的上下文参数，如果没有则返回第二个参数的默认值
     * @param key
     * @param defaultValue
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttributeOrDefault(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    /**
     * 根据过滤器id获取对应的过滤器配置信息
     * @param filterId
     * @return
     */
    public Rule.FilterConfig getFilterConfig(String filterId) {
        return rule.getFilterConfig(filterId);
    }

    /**
     * 获取上下文中唯一的UniqueId
     * @return
     */
    public String getUniqueId() {
        return request.getUniqueId();
    }

    /**
     * 重写父类释放资源：basicContext的该方法，主要用于真正的释放操作
     */
    public void releaseRequest() {
        if(requestReleased.compareAndSet(false, true)) {
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    /**
     * 调用该方法就是获取原始请求内容，不去做任何修改动作
     * @return
     */
    public GatewayRequest getOriginRequest() {
        return request;
    }

    @Override
    public GatewayRequest getRequest() {
        return request;
    }

    @Override
    public GatewayResponse getResponse() {
        return response;
    }

    public void setResponse(GatewayResponse response) {
        this.response = (GatewayResponse) response;
    }

    @Override
    public Rule getRule() {
        return rule;
    }
}