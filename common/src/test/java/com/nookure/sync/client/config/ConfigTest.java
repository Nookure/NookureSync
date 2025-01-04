package com.nookure.sync.client.config;

import com.nookure.sync.config.ConfigurationContainer;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class ConfigTest {
  @Configuration
  public static class Config {
    @Comment("This is a key")
    public String key = "value";
  }

  @Test
  public void basicTest() {
    var path = Path.of("build", "test", "config");

    var container = ConfigurationContainer.load(path, "config.yml", Config.class);
    var config = container.get();
    config.key = "new value";
    container.save().join();

    assert container.get().key.equals("new value");
  }
}
