package com.cool.client.core;

import com.cool.client.support.dubbo.DubboConstants;
import com.cool.common.config.DubboServiceInvoker;
import com.cool.common.config.HttpServiceInvoker;
import com.cool.common.config.ServiceDefinition;
import com.cool.common.config.ServiceInvoker;
import com.cool.common.constants.BasicConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.ServiceBean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解扫描类
 */
public class ApiAnnotationScanner {
    private ApiAnnotationScanner() {
    }

    private static class SingletonHolder {
        static final ApiAnnotationScanner INSTANCE = new ApiAnnotationScanner();
    }

    public static ApiAnnotationScanner getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 扫描并解析服务定义
     * 该方法用于检查给定的bean是否标记了@ApiService注解，并根据注解的信息以及bean的方法创建服务定义对象
     * 如果bean没有标记@ApiService注解，或者没有定义任何服务方法，则返回null
     *
     * @param bean 待扫描的Java对象，用于检查是否标记了@ApiService注解并获取其方法
     * @param args 可变参数，具体含义取决于协议类型，例如Dubbo协议中需要传递ServiceBean对象
     * @return 返回解析后的ServiceDefinition对象，如果无法解析则返回null
     */
    public ServiceDefinition scanner(Object bean, Object... args) {
        // 获取bean的类信息
        Class<?> aClass = bean.getClass();
        // 检查类是否标记了@ApiService注解
        if (!aClass.isAnnotationPresent(ApiService.class)) {
            return null;
        }

        // 获取@ApiService注解的实例，以读取其中的属性值
        ApiService apiService = aClass.getAnnotation(ApiService.class);
        // 从注解中获取服务ID、协议、路径和版本信息
        String serviceId = apiService.serviceId();
        ApiProtocol protocol = apiService.protocol();
        String patternPath = apiService.patternPath();
        String version = apiService.version();

        // 创建一个新的服务定义对象
        ServiceDefinition serviceDefinition = new ServiceDefinition();

        // 初始化一个映射，用于存储服务方法的路径和对应的调用器
        Map<String, ServiceInvoker> invokerMap = new HashMap<>();

        // 获取类的所有方法
        Method[] methods = aClass.getMethods();
        // 遍历所有方法，寻找标记了@ApiInvoker注解的方法
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                // 检查方法是否标记了@ApiInvoker注解
                ApiInvoker apiInvoker = method.getAnnotation(ApiInvoker.class);
                if (apiInvoker == null) {
                    continue;
                }

                // 获取方法的访问路径
                String path = apiInvoker.path();

                // 根据协议类型创建相应的服务调用器
                switch (protocol) {
                    case HTTP:
                        // 创建HTTP协议的服务调用器
                        HttpServiceInvoker httpServiceInvoker = createHttpServiceInvoker(path);
                        invokerMap.put(path, httpServiceInvoker);
                        break;
                    case DUBBO:
                        // 从参数中获取ServiceBean对象，用于Dubbo协议
                        ServiceBean<?> serviceBean = (ServiceBean<?>) args[0];
                        // 创建Dubbo协议的服务调用器
                        DubboServiceInvoker dubboServiceInvoker = createDubboServiceInvoker(path, serviceBean, method);

                        // 获取Dubbo服务的版本信息，如果存在则更新版本号
                        String dubboVersion = dubboServiceInvoker.getVersion();
                        if (!StringUtils.isBlank(dubboVersion)) {
                            version = dubboVersion;
                        }
                        invokerMap.put(path, dubboServiceInvoker);
                        break;
                    default:
                        // 如果是未知协议，则不做任何操作
                        break;
                }
            }

            // 设置服务定义的唯一ID、服务ID、版本号、协议、路径、启用状态和调用器映射
            serviceDefinition.setUniqueId(serviceId + BasicConst.COLON_SEPARATOR + version);
            serviceDefinition.setServiceId(serviceId);
            serviceDefinition.setVersion(version);
            serviceDefinition.setProtocol(protocol.getCode());
            serviceDefinition.setPatternPath(patternPath);
            serviceDefinition.setEnable(true);
            serviceDefinition.setInvokerMap(invokerMap);

            // 返回解析后的服务定义对象
            return serviceDefinition;
        }

        // 如果没有找到任何服务方法，则返回null
        return null;
    }



    /**
     * 构建HttpServiceInvoker对象
     */
    private HttpServiceInvoker createHttpServiceInvoker(String path) {
        HttpServiceInvoker httpServiceInvoker = new HttpServiceInvoker();
        httpServiceInvoker.setInvokerPath(path);
        return httpServiceInvoker;
    }

    /**
     * 构建DubboServiceInvoker对象
     */
    private DubboServiceInvoker createDubboServiceInvoker(String path, ServiceBean<?> serviceBean, Method method) {
        DubboServiceInvoker dubboServiceInvoker = new DubboServiceInvoker();
        dubboServiceInvoker.setInvokerPath(path);

        String methodName = method.getName();
        String registerAddress = serviceBean.getRegistry().getAddress();
        String interfaceClass = serviceBean.getInterface();

        dubboServiceInvoker.setRegisterAddress(registerAddress);
        dubboServiceInvoker.setMethodName(methodName);
        dubboServiceInvoker.setInterfaceClass(interfaceClass);

        String[] parameterTypes = new String[method.getParameterCount()];
        Class<?>[] classes = method.getParameterTypes();
        for (int i = 0; i < classes.length; i++) {
            parameterTypes[i] = classes[i].getName();
        }
        dubboServiceInvoker.setParameterTypes(parameterTypes);

        Integer seriveTimeout = serviceBean.getTimeout();
        if (seriveTimeout == null || seriveTimeout.intValue() == 0) {
            ProviderConfig providerConfig = serviceBean.getProvider();
            if (providerConfig!= null) {
                Integer providerTimeout = providerConfig.getTimeout();
                if (providerTimeout == null || providerTimeout.intValue() == 0) {
                    seriveTimeout = DubboConstants.DUBBO_TIMEOUT;
                } else {
                    seriveTimeout = providerTimeout;
                }
            }
        }
        dubboServiceInvoker.setTimeout(seriveTimeout);

        String dubboVersion = serviceBean.getVersion();
        dubboServiceInvoker.setVersion(dubboVersion);
        return dubboServiceInvoker;
    }

}
