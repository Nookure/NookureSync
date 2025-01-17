package com.nookure.sync.client.handler;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.nookure.sync.protocol.PacketEvent;
import com.nookure.sync.protocol.Connection;
import com.nookure.sync.protocol.Packet;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class ClientPacketHandler extends SimpleChannelInboundHandler<Packet> {
  private static final Logger logger = LoggerFactory.getLogger(ClientPacketHandler.class);
  private final AtomicReference<Connection> connection;
  private final EventBus eventBus;

  @Inject
  public ClientPacketHandler(
      @NotNull final AtomicReference<Connection> connection,
      @NotNull final EventBus eventBus
  ) {
    this.connection = connection;
    this.eventBus = eventBus;
  }

  @Override
  protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx, Packet msg) throws Exception {
    logger.debug("Received packet: {}", msg.getClass().getSimpleName());

    eventBus.post(PacketEvent.newPacketEvent(msg, connection.get()));
  }
}
