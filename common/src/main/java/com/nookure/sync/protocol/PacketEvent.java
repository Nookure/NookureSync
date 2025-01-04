package com.nookure.sync.protocol;

import org.jetbrains.annotations.NotNull;

public class PacketEvent<P extends Packet> {
  private final P packet;
  private final Connection connection;

  public PacketEvent(@NotNull P packet, @NotNull Connection connection) {
    this.packet = packet;
    this.connection = connection;
  }
  /**
   * Get the packet that was sent
   *
   * @return the packet that was sent
   */
  @NotNull public P getPacket() {
    return packet;
  }

  /**
   * Get the connection between the client and the server
   *
   * @return the connection between the client and the server
   */
  @NotNull public Connection getConnection() {
    return connection;
  }

  public static <P extends Packet> PacketEvent<P> newPacketEvent(@NotNull P packet, @NotNull Connection connection) {
    return new PacketEvent<>(packet, connection);
  }
}
