package com.nookure.sync.client;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.nookure.sync.annotation.ServerInformation;
import com.nookure.sync.client.handler.ClientPacketHandler;
import com.nookure.sync.codec.PacketDecoder;
import com.nookure.sync.codec.PacketEncoder;
import com.nookure.sync.client.config.Config;
import com.nookure.sync.client.config.ConfigurationContainer;
import com.nookure.sync.protocol.Connection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SyncClient {
  private final Logger logger = LoggerFactory.getLogger(SyncClient.class);
  private final ConfigurationContainer<Config> config;
  private final Injector injector;
  private final AtomicReference<Connection> connection;
  private final List<Object> listeners;

  @Inject
  public SyncClient(
      @NotNull ConfigurationContainer<Config> config,
      @NotNull final Injector injector,
      @NotNull final AtomicReference<Connection> connection,
      @NotNull final EventBus eventBus,
      @NotNull @ServerInformation UUID serverUUID,
      @NotNull @ServerInformation String serverName,
      @NotNull @Assisted final List<Object> listeners
  ) {
    this.config = config;
    this.injector = injector;
    this.connection = connection;
    this.listeners = listeners;

    listeners.forEach(eventBus::register);
  }

  public void connect() throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
              ChannelPipeline pipeline = channel.pipeline();

              if (config.get().client.compression) {
                pipeline.addLast("decompressor", new JdkZlibDecoder(ZlibWrapper.GZIP));
                pipeline.addLast("compressor", new JZlibEncoder(ZlibWrapper.GZIP));
              }

              pipeline.addLast("decoder", injector.getInstance(PacketDecoder.class));
              pipeline.addLast("encoder", injector.getInstance(PacketEncoder.class));

              pipeline.addLast("handler", injector.getInstance(ClientPacketHandler.class));

              connection.set(new Connection(channel));
            }
          });

      String host = config.get().client.host;
      int port = config.get().client.port;

      ChannelFuture future = bootstrap.connect(host, port).sync();
      logger.info("Connected to server at {}:{}", host, port);
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      logger.error("Error occurred while connecting to the server", e);
    } finally {
      group.shutdownGracefully();
    }
  }
}
