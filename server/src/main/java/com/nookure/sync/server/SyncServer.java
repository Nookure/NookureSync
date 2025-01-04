package com.nookure.sync.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.nookure.sync.codec.PacketDecoder;
import com.nookure.sync.codec.PacketEncoder;
import com.nookure.sync.server.config.Config;
import com.nookure.sync.client.config.ConfigurationContainer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class SyncServer {
  private final ConfigurationContainer<Config> config;
  private final Logger logger = LoggerFactory.getLogger("NookureSync");
  private final Injector injector;

  @Inject
  public SyncServer(
      @NotNull final ConfigurationContainer<Config> config,
      @NotNull final Injector injector
  ) {
    this.config = config;
    this.injector = injector;
  }

  public void start() throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
              if (config.get().server.compression) {
                channel.pipeline().addLast("compressor", new JdkZlibDecoder(ZlibWrapper.GZIP));
                channel.pipeline().addLast("decompressor", new JZlibEncoder(ZlibWrapper.GZIP));
              }

              channel.pipeline().addLast("decoder", injector.getInstance(PacketDecoder.class));
              channel.pipeline().addLast("encoder", injector.getInstance(PacketEncoder.class));

              channel.pipeline().addLast("handler", injector.getInstance(ServerHandler.class));
            }
          });

      ChannelFuture future = bootstrap.bind(new InetSocketAddress(config.get().server.host, config.get().server.port)).sync();
      logger.info("Server started on {}:{}", config.get().server.host, config.get().server.port);
      if (config.get().server.compression) logger.info("Using zlib compression.");
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      logger.error("Error occurred while running the server", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
