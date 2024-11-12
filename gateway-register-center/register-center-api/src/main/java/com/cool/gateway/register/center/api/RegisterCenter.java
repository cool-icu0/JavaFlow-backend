package com.cool.gateway.register.center.api;

import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;

public interface RegisterCenter {
    void  init(String registerAddress,String env);

    void register(ServiceDefinition serviceDefinition , ServiceInstance serviceInstance);

    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    //todo 6-2 4.23

}
