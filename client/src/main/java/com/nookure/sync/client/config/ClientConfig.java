package com.nookure.sync.client.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class ClientConfig {
  @Comment("The host the client should connect to.")
  public String host = "localhost";
  @Comment("The port the client should connect to.")
  public int port = 8080;
  @Comment("Whether the client should use compression.")
  public boolean compression = true;

  @Override
  public String toString() {
    return "ClientConfig{" +
        "host='" + host + '\'' +
        ", port=" + port +
        ", compression=" + compression +
        '}';
  }
}
