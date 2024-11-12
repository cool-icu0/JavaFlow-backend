package com.cool.gateway.register.center.api;

import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;

import java.util.Set;

public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet);
}
