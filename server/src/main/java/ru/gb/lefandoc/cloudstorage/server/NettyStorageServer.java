package ru.gb.lefandoc.cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import ru.gb.lefandoc.cloudstorage.server.handler.CloudMessageHandler;

@Slf4j
public class NettyStorageServer {

    public static void main(String[] args) {

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new CloudMessageHandler()
                            );
                            log.info("Client connected");
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            log.info("Server started...");
            future.channel().closeFuture().sync(); // block
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
            log.debug("Client disconnected");
        }
    }
}
