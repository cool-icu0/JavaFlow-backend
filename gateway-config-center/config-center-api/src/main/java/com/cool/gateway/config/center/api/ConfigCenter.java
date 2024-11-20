package com.cool.gateway.config.center.api;

public interface ConfigCenter {
    /**
     * 初始化
     */
    void init(String serverAddr,String env);
    /**
     * 订阅配置变更
     */
    void subscribeRulesChange(RulesChangeListener listener);

}
