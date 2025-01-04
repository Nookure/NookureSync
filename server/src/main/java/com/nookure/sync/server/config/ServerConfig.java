package com.nookure.sync.server.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class ServerConfig {
  @Comment("The port the server should listen on.")
  public int port = 8080;
  @Comment("The host the server should listen on.")
  public String host = "localhost";
  @Comment("Whether the server should use compression.")
  public boolean compression = true;

  @Override
  public String toString() {
    return "ServerConfig{" +
        "port=" + port +
        ", host='" + host + '\'' +
        ", compression=" + compression +
        '}';
  }
}
