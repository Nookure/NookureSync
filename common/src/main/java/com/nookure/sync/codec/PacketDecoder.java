package com.nookure.sync.codec;

import com.google.inject.Inject;
import com.nookure.sync.annotation.IsServer;
import com.nookure.sync.protocol.Packet;
import com.nookure.sync.protocol.PacketDirection;
import com.nookure.sync.protocol.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
  private final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);
  private final PacketRegistry packetRegistry;
  private final PacketDirection direction;

  @Inject
  public PacketDecoder(
      @NotNull final PacketRegistry packetRegistry,
      @IsServer final boolean isServer
  ) {
    this.packetRegistry = packetRegistry;
    this.direction = isServer ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND;
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
    if (byteBuf.readableBytes() < 2) return;

    byteBuf.markReaderIndex();
    final short id = byteBuf.readShort();
    Packet packet;

    try {
      packet = packetRegistry.createPacket(id);
    } catch (Exception e) {
      logger.error("Failed to create packet with ID {}", String.format("0x%02X", id));
      logger.error("Exception: ", e);
      byteBuf.resetReaderIndex();
      return;
    }

    packet.decode(byteBuf, direction);
    out.add(packet);

    logger.debug("Decoded packet {} with ID {}", packet.getClass().getSimpleName(), String.format("0x%02X", id));
  }
}
