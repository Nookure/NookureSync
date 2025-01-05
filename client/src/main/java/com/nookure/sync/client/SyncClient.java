package com.nookure.sync.client;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.nookure.sync.annotation.DataPath;
import com.nookure.sync.annotation.ServerInformation;
import com.nookure.sync.client.handler.ClientPacketHandler;
import com.nookure.sync.codec.PacketDecoder;
import com.nookure.sync.codec.PacketEncoder;
import com.nookure.sync.client.config.Config;
import com.nookure.sync.config.ConfigurationContainer;
import com.nookure.sync.protocol.Connection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SyncClient {
  private final Logger logger = LoggerFactory.getLogger(SyncClient.class);
  private final ConfigurationContainer<Config> config;
  private final Injector injector;
  private final AtomicReference<Connection> connection;
  private final Path path;
  private EventBus eventBus;

  @Inject
  public SyncClient(
      @NotNull ConfigurationContainer<Config> config,
      @NotNull final Injector injector,
      @NotNull final AtomicReference<Connection> connection,
      @NotNull final EventBus eventBus,
      @NotNull @ServerInformation UUID serverUUID,
      @NotNull @ServerInformation String serverName,
      @NotNull @DataPath Path path
  ) {
    this.config = config;
    this.injector = injector;
    this.connection = connection;
    this.eventBus = eventBus;
    this.path = path;
  }

  public void connect() throws InterruptedException {
    SslContext sslContext = null;

    if (config.get().ssl.enabled) {
      try {
        sslContext = createSSLContext();
      } catch (SSLException e) {
        logger.error("Error occurred while creating SSL context", e);
      }
    }

    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap();
      SslContext finalSslContext = sslContext;
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
              ChannelPipeline pipeline = channel.pipeline();
              if (finalSslContext != null)
                pipeline.addLast("ssl", finalSslContext.newHandler(channel.alloc()));

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

  public SslContext createSSLContext() throws SSLException {
    var file = path.resolve("certificates").toFile();
    if (!file.exists()) {
      if (!file.mkdirs()) {
        logger.error("Failed to create certificates directory.");
        return null;
      }
    }

    var clientCert = file.toPath().resolve(config.get().ssl.certPath).toFile();
    var clientKey = file.toPath().resolve(config.get().ssl.keyPath).toFile();
    var trustCert = file.toPath().resolve(config.get().ssl.caCertPath).toFile();

    Stream.of(clientCert, clientKey, trustCert).forEach(cert -> {
      if (!cert.exists()) {
        logger.error("Certificate file {} does not exist.", cert.getName());
      }
    });

    return SslContextBuilder.forClient()
        .keyManager(clientCert, clientKey)
        .trustManager(trustCert)
        .sslProvider(SslProvider.OPENSSL)
        .protocols("TLSv1.3", "TLSv1.2")
        .ciphers(null, SupportedCipherSuiteFilter.INSTANCE)
        .build();
  }

  public void addListener(Object listener) {
    eventBus.register(listener);
  }
}
