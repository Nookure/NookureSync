package com.nookure.sync.protocol.login;

import com.nookure.sync.protocol.Packet;
import com.nookure.sync.protocol.PacketDirection;
import com.nookure.sync.protocol.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerBoundIdentificationResponse implements Packet {
  private UUID serverId;
  private String serverName;

  public ServerBoundIdentificationResponse() {
  }

  public ServerBoundIdentificationResponse(
      @NotNull final UUID serverId,
      @NotNull final String serverName
  ) {
    this.serverId = serverId;
    this.serverName = serverName;
  }

  @Override
  public void decode(@NotNull ByteBuf buf, @NotNull PacketDirection direction) {
    serverId = PacketUtils.readUuid(buf);
    serverName = PacketUtils.readString(buf);
  }

  @Override
  public void encode(@NotNull ByteBuf buf, @NotNull PacketDirection direction) {
    PacketUtils.writeUuid(buf, serverId);
    PacketUtils.writeString(buf, serverName);
  }

  public UUID serverId() {
    return serverId;
  }

  public String serverName() {
    return serverName;
  }
}
