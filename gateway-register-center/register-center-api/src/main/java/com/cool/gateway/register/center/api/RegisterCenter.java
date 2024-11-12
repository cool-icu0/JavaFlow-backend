package com.cool.gateway.register.center.api;

import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;

public interface RegisterCenter {
    // 初始化
    void  init(String registerAddress,String env);
    // 注册服务
    void register(ServiceDefinition serviceDefinition , ServiceInstance serviceInstance);
    // 注销服务
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);
    // 订阅所有服务变更
    void subscribeAllServices(RegisterCenterListener registerCenterListener);

}
