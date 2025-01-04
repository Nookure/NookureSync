package com.nookure.sync;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.nookure.sync.annotation.IsServer;
import com.nookure.sync.server.config.Config;
import com.nookure.sync.client.config.ConfigurationContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneModule extends AbstractModule {
  private final ConfigurationContainer<Config> config;
  private final Logger logger = LoggerFactory.getLogger("NookureSync");

  public StandaloneModule(@NotNull final ConfigurationContainer<Config> config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<ConfigurationContainer<Config>>() {
    }).toInstance(config);

    bind(Logger.class).toInstance(logger);
    bind(Boolean.class).annotatedWith(IsServer.class).toInstance(true);
  }
}
