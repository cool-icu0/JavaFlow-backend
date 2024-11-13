package com.cool.gateway.register.center.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;
import com.cool.gateway.register.center.api.RegisterCenter;
import com.cool.gateway.register.center.api.RegisterCenterListener;

import java.util.ArrayList;
import java.util.List;

public class NacosRegisterCenter implements RegisterCenter {

    private String registerAddress ;

    private String env ;

    //主要用于维护服务实例信息
    private NamingService namingService;

    //主要用于维护服务定义信息
    private NamingMaintainService namingMaintainService;

    //监听器列表
    private List<RegisterCenterListener> registerCenterListenerList;



    @Override
    public void init(String registerAddress, String env) {
        this.registerAddress = registerAddress;
        this.env = env;

        try {
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(registerAddress);
            this.namingService = NamingFactory.createNamingService(registerAddress);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            // 构造nacos服务实例信息
            Instance nacosInstance = new Instance();
            nacosInstance.setInstanceId(serviceInstance.getServiceInstanceId());


        }
        catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {

    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {

    }
}
