package com.cool.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 核心上下文接口定义
 */
public interface IContext {
    /**
     * 一个请求正在执行中的状态
     */
    int RUNNING = 0;

    /**
     * 标志请求结束，写回Response
     */
    int WRITTEN = 1;
    /**
     * 写回成功后，设置该标识，如果是Netty ，ctx.WriteAndFlush(response)
     */
    int COMPLETED = 2;

    /**
     * 整个网关请求完毕，彻底结束
     */
    int TERMINATED=-1;

    /**
     * 设置上下文状态为运行中
     */
    void runned();

    /**
     * 设置上下文状态为写回
     */
    void written();

    /**
     * 设置上下文状态为标记写回成功
     */
    void completed();

    /**
     * 设置上下文状态为标记写回成功
     */
    void terminated();

    /**
     * 判断网关运行状态
     * @return
     */
    boolean isRunning();
    boolean isWritten();
    boolean isCompleted();
    boolean isTerminated();

    /**
     * 获取请求转换协议
     * @return
     */
    String getProtocol();

    //todo 这里还有一个规则获取的方法，暂时不写
//    /**
//     * 获取请求转换协议
//     * @return
//     */
//    Rule getRule();
    /**
     * 获取请求对象
     * @return
     */
    Object getRequest();

    /**
     * 获取返回对象
     * @return
     */
    Object getResponse();

    /**
     * 获取异常信息
     */
    Throwable getThrowable();

    //todo 这里还有一个getAttribute方法，暂时不写
//    /**
//     * 获取上下文参数
//     * @return
//     */
//    Object getAttribute(Map<String,Object> key);

    //todo 这里还有一个setRule方法，暂时不写
//    /**
//     * 设置请求规则
//     * @return
//     */
//    void setRule();
    /**
     * 设置请求返回结果
     * @return
     */
    void setResponse();
    /**
     * 设置请求异常信息
     * @return
     */
    void setThrowable(Throwable throwable);

    //todo 这里还有一个setAttribute方法，暂时不写
//    /**
//     * 设置上下文参数
//     * @return
//     */
//    void setAttribute(String key,Object obj);

    /**
     * 获取Netty上下文
     * @return
     */
    ChannelHandlerContext getNettyCtx();

    /**
     * 是否保持连接
     * @return
     */
    boolean isKeepAlive();

    /**
     * 释放请求资源
     * @return
     */
    boolean releaseRequest();

    /**
     * 设置写回接收回调函数
     * @param consumer
     */
    void setCompletedCallBack(Consumer<IContext> consumer);

    /**
     * 执行写回接收回调函数
     * @param consumer
     */
    void invokeCompletedCallBack(Consumer<IContext> consumer);

}
