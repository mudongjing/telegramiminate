package com.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseServer implements Server{
    protected Logger logger=LoggerFactory.getLogger(getClass());
    protected DefaultEventLoopGroup defaultEventLoopGroup;
    protected NioEventLoopGroup bossGroup;
    protected NioEventLoopGroup workerGroup;
    protected ServerBootstrap serverBootstrap;
    protected int port;//地址端口
    protected  String host="127.0.0.1";
    protected ChannelFuture channelFuture;

    public void init() {
        defaultEventLoopGroup=new DefaultEventLoopGroup(3, new ThreadFactory() {
            private AtomicInteger index=new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"DefaultEventLoopGroup-"+index.incrementAndGet());
            }
        });
        bossGroup=new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger index=new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"BossGroup-"+index.incrementAndGet());
            }
        });
        workerGroup=new NioEventLoopGroup(8, new ThreadFactory() {
            private AtomicInteger index=new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"WorkerGroup-"+index.incrementAndGet());
            }
        });
        serverBootstrap=new ServerBootstrap();
    }

    @Override
    public void finish() {
        if(defaultEventLoopGroup!=null){
            defaultEventLoopGroup.shutdownGracefully();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
