package com.nookure.sync.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Singleton;
import com.nookure.sync.exception.PacketInstantiationException;
import com.nookure.sync.exception.PacketWithoutDefaultConstructorException;
import com.nookure.sync.protocol.login.ClientBoundIdentificationRequestPacket;
import com.nookure.sync.protocol.login.ServerBoundIdentificationResponse;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Singleton
public class PacketRegistry {
  private final BiMap<Short, Class<? extends Packet>> packets = HashBiMap.create();

  {
    registerPacket((short) 0x00, ClientBoundIdentificationRequestPacket.class);
    registerPacket((short) 0x01, ServerBoundIdentificationResponse.class);
  }

  /**
   * Register a packet by its id
   *
   * @param id     The id of the packet (e.g. 0x00)
   * @param packet The packet class
   */
  public void registerPacket(final short id, @NotNull final Class<? extends Packet> packet) {
    requireNonNull(packet, "Packet class cannot be null");

    synchronized (packets) {
      if (packets.containsKey(id)) {
        throw new IllegalArgumentException("Packet with id " + id + " is already registered");
      }

      packets.put(id, packet);
    }
  }

  /**
   * Get the packet class by its id
   *
   * @param id The id of the packet
   * @return The packet class
   */
  public Class<? extends Packet> getPacket(short id) {
    synchronized (packets) {
      return packets.get(id);
    }
  }

  /**
   * Get the id of a packet
   *
   * @param packet The packet class
   * @return The id of the packet
   */
  public short getPacketId(@NotNull final Class<? extends Packet> packet) {
    requireNonNull(packet, "Packet class cannot be null");

    synchronized (packets) {
      return packets.inverse().get(packet);
    }
  }

  /**
   * Create a new packet instance by its id
   *
   * @param id The id of the packet
   * @return The packet instance
   */
  @NotNull
  public Packet createPacket(final short id) {
    synchronized (packets) {
      Class<? extends Packet> packet = packets.get(id);
      if (packet == null) {
        throw new IllegalArgumentException("Unknown packet id " + id);
      }

      try {
        return packet.getConstructor().newInstance();
      } catch (NoSuchMethodException e) {
        throw new PacketWithoutDefaultConstructorException("No default constructor found for packet", e);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new PacketInstantiationException("Failed to create packet instance", e);
      }
    }
  }

  /**
   * Get the map of packet ids to packet classes
   *
   * @return The map
   */
  public Map<Short, Class<? extends Packet>> getIdToPacketMap() {
    return packets;
  }

  /**
   * Get the map of packet classes to packet ids
   *
   * @return The map
   */
  public Map<Class<? extends Packet>, Short> getPacketToIdMap() {
    return packets.inverse();
  }

  public BiMap<Short, Class<? extends Packet>> getPacketMap() {
    return packets;
  }
}
