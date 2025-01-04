package com.nookure.sync.client.module;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.nookure.sync.annotation.IsServer;
import com.nookure.sync.annotation.ServerInformation;
import com.nookure.sync.client.SyncClient;
import com.nookure.sync.client.SyncClientFactory;
import com.nookure.sync.client.config.Config;
import com.nookure.sync.client.config.ConfigurationContainer;
import com.nookure.sync.protocol.Connection;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientModule extends AbstractModule {
  private final ConfigurationContainer<Config> config;
  private final UUID serverUUID;
  private final String serverName;

  public ClientModule(
      @NotNull final ConfigurationContainer<Config> config,
      @NotNull final UUID serverUUID,
      @NotNull final String serverName
  ) {
    super();
    this.config = config;
    this.serverUUID = serverUUID;
    this.serverName = serverName;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<ConfigurationContainer<Config>>() {
    }).toInstance(config);
    bind(new TypeLiteral<AtomicReference<Connection>>() {
    }).toInstance(new AtomicReference<>(null));

    bind(Boolean.class).annotatedWith(IsServer.class).toInstance(false);
    bind(EventBus.class).toInstance(new EventBus());

    bind(UUID.class)
        .annotatedWith(ServerInformation.class)
        .toInstance(serverUUID);

    bind(String.class)
        .annotatedWith(ServerInformation.class)
        .toInstance(serverName);

    install(new FactoryModuleBuilder()
        .implement(SyncClient.class, SyncClient.class)
        .build(SyncClientFactory.class)
    );
  }
}
