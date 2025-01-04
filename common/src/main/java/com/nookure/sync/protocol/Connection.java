package com.nookure.sync.protocol;

import com.google.inject.assistedinject.Assisted;
import io.netty.channel.ChannelFuture;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Connection {
  private final SocketChannel channel;

  public Connection(@NotNull @Assisted final SocketChannel channel) {
    this.channel = channel;
  }

  public ChannelFuture sendPacket(@NotNull Packet packet) {
    return channel.writeAndFlush(packet);
  }

  <T extends Packet> UUID registerHandler(@NotNull Class<T> packetClass, @NotNull SimpleChannelInboundHandler<T> handler) {
    final UUID id = UUID.randomUUID();
    channel.pipeline().addLast(id.toString(), handler);
    return id;
  }

  public void unregisterHandler(@NotNull UUID id) {
    channel.pipeline().remove(id.toString());
  }

  public CompletableFuture<Void> close() {
    final CompletableFuture<Void> future = new CompletableFuture<>();
    channel.close().addListener((ChannelFuture f) -> {
      if (f.isSuccess()) {
        future.complete(null);
      } else {
        future.completeExceptionally(f.cause());
      }
    });
    return future;
  }
}
