package com.nookure.sync.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nookure.sync.client.config.Config;
import com.nookure.sync.config.ConfigurationContainer;
import com.nookure.sync.client.module.ClientModule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.UUID;

public class ClientTest {
  private static final UUID SERVER_UUID = UUID.randomUUID();
  private static final String SERVER_NAME = "TestServer";

  @Test
  public void test() {
    Logger logger = LoggerFactory.getLogger(ClientTest.class);

    var path = Path.of("build", "test", "config");
    ConfigurationContainer<Config> container = ConfigurationContainer.load(path, "client.yml", Config.class);

    Injector injector = Guice.createInjector(new ClientModule(
        container,
        SERVER_UUID,
        SERVER_NAME,
        Path.of(System.getProperty("user.dir")).resolve("build/test"))
    );

    SyncClient client = injector.getInstance(SyncClient.class);

    try {
      client.connect();
    } catch (Exception e) {
      logger.error(String.valueOf(e));
    }
  }
}
