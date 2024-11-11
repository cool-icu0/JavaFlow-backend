package com.cool.core.netty;

import com.cool.common.utils.RemotingUtil;
import com.cool.core.Config;
import com.cool.core.LifeCycle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.jmx.Server;

import java.net.InetSocketAddress;

@Slf4j
public class NettyHttpServer  implements LifeCycle {
    private final Config config;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup eventLoopGroupBoss;
    @Getter
    private EventLoopGroup eventLoopGroupWorker;
    private final NettyProcessor nettyProcessor;

    public NettyHttpServer(Config config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init();
    }

    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();

        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("netty-boss-nio"));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(config.getEventLoopGroupWokerNum(),
                    new DefaultThreadFactory("netty-woker-nio"));
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("netty-boss-nio"));
            this.eventLoopGroupWorker = new NioEventLoopGroup(config.getEventLoopGroupWokerNum(),
                    new DefaultThreadFactory("netty-woker-nio"));
        }
    }
    public  boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

    @Override
    public void start() {
        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(config.getPort()))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(
                                new HttpServerCodec(), //http编解码
                                new HttpObjectAggregator(config.getMaxContentLength()),//http对象聚合器
                                new NettyServerConnectManagerHandler(),
                                new NettyHttpServerHandler(nettyProcessor)
                        );
                    }
                });
        try {
            this.serverBootstrap.bind().sync();;
            log.info("server startup on port {}", this.config.getPort());
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void shutdown() {
        if (eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully();
        }
        if (eventLoopGroupWorker != null) {
            eventLoopGroupWorker.shutdownGracefully();
        }
    }
}
