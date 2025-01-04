package com.nookure.sync.server.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class Config {
  @Comment("Here you can configure the netty server configuration.")
  public ServerConfig server = new ServerConfig();

  @Override
  public String toString() {
    return "Config{" +
        "server=" + server +
        '}';
  }
}
