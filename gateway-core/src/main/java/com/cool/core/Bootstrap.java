package com.cool.core;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import com.cool.common.config.DynamicConfigManager;
import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInstance;
import com.cool.common.utils.NetUtils;
import com.cool.common.utils.TimeUtil;
import com.cool.gateway.config.center.api.ConfigCenter;
import com.cool.gateway.register.center.api.RegisterCenter;
import com.cool.gateway.register.center.api.RegisterCenterListener;



import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import static com.cool.common.constants.BasicConst.COLON_SEPARATOR;

/**
 * API网关启动类
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        //加载网关静态核心配置
        Config config = ConfigLoader.getInstance().load(args);
        System.out.println(config.getPort());
        //插件初始化
        //配置中心管理器初始化，连接配置中心，监听配置的新增、修改、删除
        ServiceLoader<ConfigCenter> serviceLoader = ServiceLoader.load(ConfigCenter.class);
        final ConfigCenter configCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found ConfigCenter impl");
            return new RuntimeException("not found ConfigCenter impl");
        });
        configCenter.init(config.getRegistryAddress(), config.getEnv());
        configCenter.subscribeRulesChange(rules -> DynamicConfigManager.getInstance()
            .putAllRule(rules));

        //启动容器
        Container container = new Container(config);
        container.start();
        //注册中心，将注册中心的实例加载到本地
        final RegisterCenter registerCenter = registerAndSubscribe(config);

        //服务优雅关机
        //收到kill信号时调用
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // 执行优雅关机操作
                // 注销网关服务实例
                registerCenter.deregister(buildGatewayServiceDefinition(config),
                        buildGatewayServiceInstance(config));
                container.shutdown();
            }
        });
    }

    private static RegisterCenter registerAndSubscribe(Config config) {
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        final RegisterCenter registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        registerCenter.init(config.getRegistryAddress(), config.getEnv());

        //构造网关服务定义和实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //注册网关服务实例
        registerCenter.register(serviceDefinition, serviceInstance);

        //订阅所有服务变更
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                //服务实例变更
                log.info("refresh service and instance:{} {}", serviceDefinition.getUniqueId(),
                        JSON.toJSON(serviceInstanceSet));
                DynamicConfigManager manager = DynamicConfigManager.getInstance();
                manager.addServiceInstance(serviceInstance.getUniqueId(), serviceInstanceSet);

            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(final Config config) {
        String localIp = NetUtils.getLocalIp();
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        return serviceInstance;
    }

    /**
     * 构建网关服务定义
     * <p>
     * 该方法根据配置信息创建并初始化一个ServiceDefinition对象，用于定义网关服务的基本信息
     * 它设置了服务的唯一ID、服务ID和环境类型，以及一个空的调用者映射
     *
     * @param config 配置对象，包含应用名称和环境类型等信息
     * @return 返回初始化后的ServiceDefinition对象
     */
    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        // 创建一个新的ServiceDefinition实例
        ServiceDefinition serviceDefinition = new ServiceDefinition();

        // 设置空的调用者映射，表示当前服务没有关联任何调用者
        serviceDefinition.setInvokerMap(Map.of());

        // 设置服务的唯一ID为配置中的应用名称
        serviceDefinition.setUniqueId(config.getApplicationName());

        // 设置服务ID为配置中的应用名称，与唯一ID相同
        serviceDefinition.setServiceId(config.getApplicationName());

        // 设置服务的环境类型，根据配置信息
        serviceDefinition.setEnvType(config.getEnv());

        // 返回初始化后的服务定义对象
        return serviceDefinition;
    }
}
