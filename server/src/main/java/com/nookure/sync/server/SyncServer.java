package com.nookure.sync.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.nookure.sync.annotation.DataPath;
import com.nookure.sync.codec.PacketDecoder;
import com.nookure.sync.codec.PacketEncoder;
import com.nookure.sync.server.config.Config;
import com.nookure.sync.config.ConfigurationContainer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SyncServer {
  private final ConfigurationContainer<Config> config;
  private final Logger logger = LoggerFactory.getLogger("NookureSync");
  private final Injector injector;
  private final Path path;

  @Inject
  public SyncServer(
      @NotNull final ConfigurationContainer<Config> config,
      @NotNull final Injector injector,
      @NotNull @DataPath final Path path
  ) {
    this.config = config;
    this.injector = injector;
    this.path = path;
  }

  public void start() throws InterruptedException {
    SslContext sslContext = null;

    try {
      if (config.get().ssl.enabled)
        sslContext = createSSLContext();
    } catch (SSLException e) {
      logger.error("Failed to create SSL context.", e);
      return;
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      SslContext finalSslContext = sslContext;
      bootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
              if (config.get().ssl.enabled && finalSslContext != null) {
                channel.pipeline().addLast(finalSslContext.newHandler(channel.alloc()));
              }

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

  public SslContext createSSLContext() throws SSLException {
    var file = path.resolve("certificates").toFile();
    if (!file.exists()) {
      if (!file.mkdirs()) {
        logger.error("Failed to create certificates directory.");
        return null;
      }
    }

    var serverCert = file.toPath().resolve(config.get().ssl.certPath).toFile();
    var serverKey = file.toPath().resolve(config.get().ssl.keyPath).toFile();
    var trustCert = file.toPath().resolve(config.get().ssl.caCertPath).toFile();

    Stream.of(serverCert, serverKey, trustCert).forEach(cert -> {
      if (!cert.exists()) {
        logger.error("Certificate file {} does not exist, path {}", cert.getName(), cert.getAbsolutePath());
      }
    });

    return SslContextBuilder.forServer(serverCert, serverKey)
        .trustManager(trustCert)
        .protocols("TLSv1.3", "TLSv1.2")
        .sslProvider(SslProvider.OPENSSL)
        .ciphers(null, SupportedCipherSuiteFilter.INSTANCE)
        .build();
  }
}
