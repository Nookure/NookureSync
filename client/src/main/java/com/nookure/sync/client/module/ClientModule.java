package com.nookure.sync.client.module;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.nookure.sync.annotation.DataPath;
import com.nookure.sync.annotation.IsServer;
import com.nookure.sync.annotation.ServerInformation;
import com.nookure.sync.client.config.Config;
import com.nookure.sync.config.ConfigurationContainer;
import com.nookure.sync.protocol.Connection;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientModule extends AbstractModule {
  private final ConfigurationContainer<Config> config;
  private final UUID serverUUID;
  private final String serverName;
  private final Path path;

  public ClientModule(
      @NotNull final ConfigurationContainer<Config> config,
      @NotNull final UUID serverUUID,
      @NotNull final String serverName,
      @NotNull final Path path
  ) {
    super();
    this.config = config;
    this.serverUUID = serverUUID;
    this.serverName = serverName;
    this.path = path;
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

    bind(Path.class)
        .annotatedWith(DataPath.class)
        .toInstance(path);

    bind(String.class)
        .annotatedWith(ServerInformation.class)
        .toInstance(serverName);
  }
}
