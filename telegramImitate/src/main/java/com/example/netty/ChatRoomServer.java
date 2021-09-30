package com.example.netty;

import com.example.netty.handler.MessageHandler;
import com.example.netty.handler.UserAuthHandler;
import com.example.netty.server.BaseServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

public class ChatRoomServer extends BaseServer {
    public ChatRoomServer(int port){
        this.port=port;
    }

    @Override
    public void start() {
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//服务器通道实现
                .option(ChannelOption.SO_KEEPALIVE,true)//存活检测
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024)//队列大小
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventLoopGroup,
                                //编码解码器
                                new HttpServerCodec(),
                                //将多个消息转换成单一的消息对象
                                new HttpObjectAggregator(65536),
                                //检测链路是否读空闲,配合心跳handler检测channel是否正常
                                new IdleStateHandler(60, 0, 0),
                                // 用户认证
                                new UserAuthHandler(),
                                // 消息发送
                                new MessageHandler());
                    }
                });
        try{
            channelFuture=serverBootstrap.bind().sync();
            InetSocketAddress inetSocketAddress=(InetSocketAddress)channelFuture.channel().localAddress();
            logger.info("WebSocketServer start success, port is:{}", inetSocketAddress.getPort());
        }catch (InterruptedException e){
            logger.error("WebSocketServer start fail,", e);
        }
    }

}
