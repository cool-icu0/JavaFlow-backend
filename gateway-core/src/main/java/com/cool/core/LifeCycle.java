package com.cool.core;

// 生命周期接口
public interface LifeCycle {
    // 初始化
    void init();

    // 启动
    void start();

    // 关闭
    void shutdown();
}
