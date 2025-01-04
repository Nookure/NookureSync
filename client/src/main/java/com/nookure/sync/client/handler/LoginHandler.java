package com.nookure.sync.client.handler;

import com.google.common.eventbus.Subscribe;
import com.nookure.sync.protocol.PacketEvent;
import com.nookure.sync.protocol.login.ClientBoundIdentificationRequestPacket;
import org.jetbrains.annotations.NotNull;

public class LoginHandler {
  @Subscribe
  public void onLoginRequest(@NotNull final PacketEvent<ClientBoundIdentificationRequestPacket> container) {

  }
}
