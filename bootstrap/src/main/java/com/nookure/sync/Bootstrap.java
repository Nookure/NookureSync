package com.nookure.sync;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nookure.sync.config.Config;
import com.nookure.sync.config.ConfigurationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Bootstrap {
  public static final Logger logger = LoggerFactory.getLogger("NookureSync");

  public static void main(String[] args) {
    Thread.currentThread().setContextClassLoader(Bootstrap.class.getClassLoader());
    logger.info("Starting NookureSync server...");
    logger.debug("Debug mode enabled.");

    Path currentPath = Path.of(System.getProperty("user.dir"));
    logger.debug("Current path: {}", currentPath);

    logger.debug("Loading configuration...");
    ConfigurationContainer<Config> container = ConfigurationContainer.load(currentPath, "config.yml", Config.class);

    logger.debug("Creating injector with -> {}...", StandaloneModule.class.getName());
    Injector injector = Guice.createInjector(new StandaloneModule(container));

    logger.debug("Injecting {}...", SyncServer.class.getName());
    SyncServer server = injector.getInstance(SyncServer.class);

    try {
      logger.debug("Starting server...");
      server.start();
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
  }
}
