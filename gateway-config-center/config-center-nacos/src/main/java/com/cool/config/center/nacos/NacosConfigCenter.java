package com.cool.config.center.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cool.common.rule.Rule;
import com.cool.gateway.config.center.api.ConfigCenter;
import com.cool.gateway.config.center.api.RulesChangeListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class NacosConfigCenter implements ConfigCenter {

    private static final String DATA_ID = "cool-gateway";

    private String serverAddr;
    private String env;
    private ConfigService configService;

    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;

        // 初始化配置中心
        try {
            configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeRuleChange(RulesChangeListener listener) {
        // 订阅配置变更
        try {
            // 1、监听配置变更
            String config = configService.getConfig(DATA_ID, env, 5000);
            //{"rules":[{},{}]}
            log.info("获取到的配置为：{}", config);
            List<Rule> rules = JSON.parseObject(config).getJSONArray("rules").toJavaList(Rule.class);
            listener.onRulesChange(rules);
            // 2、监听变化，重新获取配置
            configService.addListener(DATA_ID,env , new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("配置发生变更，重新获取配置：{}", configInfo);
                    List<Rule> rules = JSON.parseObject(configInfo).getJSONArray("rules").toJavaList(Rule.class);
                    listener.onRulesChange(rules);
                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
