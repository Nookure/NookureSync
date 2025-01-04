package com.nookure.sync.protocol.login;

import com.nookure.sync.protocol.Packet;
import com.nookure.sync.protocol.PacketDirection;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class ClientBoundIdentificationRequestPacket implements Packet {
  @Override
  public void decode(@NotNull ByteBuf buf, @NotNull PacketDirection direction) {

  }

  @Override
  public void encode(@NotNull ByteBuf buf, @NotNull PacketDirection direction) {

  }
}
