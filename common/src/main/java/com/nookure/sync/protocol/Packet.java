package com.nookure.sync.protocol;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet {
  void decode(@NotNull final ByteBuf buf, @NotNull final PacketDirection direction);

  void encode(@NotNull final ByteBuf buf, @NotNull final PacketDirection direction);

  boolean handle(@NotNull final PacketHandler handler);
}
