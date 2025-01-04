package com.nookure.sync.codec;

import com.google.inject.Inject;
import com.nookure.sync.annotation.IsServer;
import com.nookure.sync.protocol.Packet;
import com.nookure.sync.protocol.PacketDirection;
import com.nookure.sync.protocol.PacketRegistry;
import com.nookure.sync.protocol.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes packets into bytes.
 *
 * <p>Each packet is encoded into a byte buffer. The packet ID is written first, followed by the
 * packet's data. The packet ID is used to identify the packet when decoding.
 * </p>
 *
 * @see Packet
 * @see PacketRegistry
 * @see PacketDirection
 *
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
  private final Logger logger = LoggerFactory.getLogger(PacketEncoder.class);
  private final PacketRegistry packetRegistry;
  private final PacketDirection direction;

  @Inject
  public PacketEncoder(
      @NotNull final PacketRegistry packetRegistry,
      @IsServer final boolean isServer
  ) {
    this.packetRegistry = packetRegistry;
    this.direction = isServer ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND;
  }

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
    if (packet == null) {
      logger.error("Packet is null, skipping encoding");
      return;
    }

    final short id = packetRegistry.getPacketId(packet.getClass());
    logger.debug("Encoding packet {} with ID {}", packet.getClass().getSimpleName(), String.format("0x%02X", id));
    byteBuf.writeShort(id);
    packet.encode(byteBuf, direction);
  }
}
