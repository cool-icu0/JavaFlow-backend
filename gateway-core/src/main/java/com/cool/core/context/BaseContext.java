package com.cool.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BaseContext implements IContext {

    //状态转发协议
    protected final String protocol;

    //状态，多线程情况下考虑使用volatile
    protected volatile int status =IContext.RUNNING;

    //Netty 上下文
    protected final ChannelHandlerContext nettyCtx;

    //Netty 上下文
    protected final Map<String,Object> attributes = new Map<String, Object>();

    //请求过程中发生的异常
    protected Throwable throwable;

    //是否保持长连接
    protected final boolean keepAlive;

    //存放回调函数集合
    protected List<Consumer<IContext>> completedCallBacks;

    //定义是否已经释放资源
    protected AtomicBoolean requestRelease = new AtomicBoolean(false);






    @Override
    public void runned() {

    }

    @Override
    public void written() {

    }

    @Override
    public void completed() {

    }

    @Override
    public void terminated() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isWritten() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public void getThrowable() {

    }

    @Override
    public void setResponse() {

    }

    @Override
    public void setThrowable(Throwable throwable) {

    }

    @Override
    public ChannelHandlerContext getNettyCtx() {
        return null;
    }

    @Override
    public boolean isKeepAlive() {
        return false;
    }

    @Override
    public boolean releaseRequest() {
        return false;
    }

    @Override
    public void setCompletedCallBack(Consumer<IContext> consumer) {

    }

    @Override
    public void invokeCompletedCallBack(Consumer<IContext> consumer) {

    }
}
