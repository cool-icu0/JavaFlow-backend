package com.cool.core.netty;

import com.cool.core.Config;
import com.cool.core.LifeCycle;
import com.cool.core.netty.processor.NettyCoreProcessor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Container implements LifeCycle {

    private final Config config;

    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }

    @Override
    public void init() {
        this.nettyProcessor = new NettyCoreProcessor();

        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);
        this.nettyHttpClient = new NettyHttpClient(config,
                nettyHttpServer.getEventLoopGroupWorker());
    }

    @Override
    public void start() {
        nettyHttpServer.start();;
        nettyHttpClient.start();
        log.info("api gateway started!");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}
