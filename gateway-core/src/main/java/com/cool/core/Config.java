package com.cool.core;


import lombok.Data;

@Data
public class Config {

    private int port =8888;

    private String appliactionName = "JavaFlow";

    private String registryAddress = "127.0.0.1:8848";

    private String env = "dev";

    //netty

    // boss线程组
    private int eventLoopGroupBossNum = 1;

    // work线程组
    private int eventLoopGroupWokerNum = Runtime.getRuntime().availableProcessors();
    // http报文最大内容长度
    private int maxContentLength = 64 * 1024 * 1024;

    //默认单异步模式
    private boolean whenComplete = true;

    //扩展.......
}
