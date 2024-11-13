package com.cool.gateway.register.center.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;
import com.cool.common.constants.GatewayConst;
import com.cool.gateway.register.center.api.RegisterCenter;
import com.cool.gateway.register.center.api.RegisterCenterListener;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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

    /**
     * 注册服务定义和服务实例到Nacos
     *
     * @param serviceDefinition 服务定义，包含服务的相关信息
     * @param serviceInstance 服务实例，包含具体实例的详细信息
     * @throws RuntimeException 如果注册过程中发生Nacos异常，则抛出运行时异常
     */
    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            // 构造nacos服务实例信息
            Instance nacosInstance = new Instance();
            nacosInstance.setInstanceId(serviceInstance.getServiceInstanceId());
            nacosInstance.setPort(serviceInstance.getPort());
            nacosInstance.setIp(serviceInstance.getIp());
            nacosInstance.setMetadata(Map.of(GatewayConst.META_DATA_KEY,
                    JSON.toJSONString(serviceInstance))
            );
            // 注册服务
            namingService.registerInstance(serviceDefinition.getServiceId(),env,nacosInstance);
            //更新服务定义
            namingMaintainService.updateService(serviceDefinition.getServiceId(),env,0,
                    Map.of(GatewayConst.META_DATA_KEY, JSON.toJSONString(serviceDefinition))
            );
            log.info("register {} {}", serviceDefinition, serviceInstance);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注销服务实例
     *
     * 当服务实例不再可用或需要被移除时，调用此方法将其从服务列表中删除
     * 这是一个重写方法，实现了自定义的注销逻辑
     *
     * @param serviceDefinition 服务定义，包含了服务的标识信息，如服务ID
     * @param serviceInstance 服务实例，包含了实例的详细信息，如IP和端口
     */
    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            namingService.registerInstance(serviceDefinition.getServiceId(),
                    env, serviceInstance.getIp(), serviceInstance.getPort());
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 订阅所有服务的方法
     * 当有新的服务注册到注册中心时，通过此方法可以自动发现并订阅这些服务
     *
     * @param registerCenterListener 注册中心监听器，用于接收注册中心的事件通知
     */
    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {
        registerCenterListenerList.add(registerCenterListener);
        doSubscribeAllServices();

        //可能有新服务加入，所以需要有一个定时任务来检查
        ScheduledExecutorService scheduledThreadPool = Executors
                .newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServices"));
        scheduledThreadPool.scheduleWithFixedDelay(() -> doSubscribeAllServices(),
                10, 10, TimeUnit.SECONDS);
    }


    private void doSubscribeAllServices() {
        try {
            //已经订阅的服务
            Set<String> subscribeService = namingService.getSubscribeServices().stream()
                    .map(ServiceInfo::getName).collect(Collectors.toSet());


            int pageNo = 1;
            int pageSize = 100;

            //nacos事件监听器
            EventListener eventListener = new NacosRegisterListener();

            //分页从nacos拿到服务列表
            List<String> serviseList = namingService
                    .getServicesOfServer(pageNo, pageSize, env).getData();

            while (CollectionUtils.isNotEmpty(serviseList)) {
                log.info("service list size {}", serviseList.size());

                for (String service : serviseList) {
                    if (subscribeService.contains(service)) {
                        continue;
                    }

                    namingService.subscribe(service, eventListener);
                    log.info("subscribe {} {}", service, env);
                }

                serviseList = namingService
                        .getServicesOfServer(++pageNo, pageSize, env).getData();
            }

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    //nacos事件监听器（内部类）
    public class NacosRegisterListener implements EventListener {

        @Override
        public void onEvent(Event event) {

        }
    }
}
