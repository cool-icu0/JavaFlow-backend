package com.cool.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


/**
 * 核心上下文基础类
 */
public class BaseContext implements IContext {

    //状态转发协议
    protected final String protocol;

    //上下文状态，多线程情况下考虑使用volatile
    protected volatile int status = IContext.RUNNING;

    //Netty 上下文
    protected final ChannelHandlerContext nettyCtx;

    //上下文参数集合
    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    //请求过程中发生的异常
    protected Throwable throwable;

    //是否保持长连接
    protected final boolean keepAlive;

    //存放回调函数集合
    protected List<Consumer<IContext>> completedCallBacks;

    //定义是否已经释放资源
    protected AtomicBoolean requestRelease = new AtomicBoolean(false);

    public BaseContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }


    @Override
    public void runned() {
        status = IContext.RUNNING;
    }

    @Override
    public void written() {
        status = IContext.WRITTEN;
    }

    @Override
    public void completed() {
        status = IContext.COMPLETED;
    }

    @Override
    public void terminated() {
        status = IContext.TERMINATED;
    }

    @Override
    public boolean isRunning() {
        return status == IContext.RUNNING;
    }

    @Override
    public boolean isWritten() {
        return status == IContext.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.TERMINATED;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }
    //todo 待实现Rule
//    @Override
//    public Rule getRule() {
//        return null;
//    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }
    //todo 待实现Getattr
//    @Override
//    public Object getAttribute(Map<String, Object> key) {
//        return attributes.get(key);
//    }
    //todo 待实现setRule
//    @Override
//    public void setRule() {
//
//    }

    @Override
    public void setResponse() {

    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    //todo 待实现setAttribute
//    @Override
//    public void setAttribute(String key,Object obj) {
//        attributes.put(key,obj);
//    }

    @Override
    public ChannelHandlerContext getNettyCtx() {
        return this.nettyCtx;
    }

    @Override
    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public boolean releaseRequest() {
        return false;
        //todo 待实现
//        this.requestReleased.compareAndSet(false,true);
    }

    @Override
    public void setCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallBacks==null){
            completedCallBacks = new ArrayList<>();
        }else {
            completedCallBacks.add(consumer);
        }
    }

    @Override
    public void invokeCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallBacks!=null){
            completedCallBacks.forEach(call->call.accept(this));
        }
    }
}
