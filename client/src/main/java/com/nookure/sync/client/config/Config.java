package com.nookure.sync.client.config;

import de.exlll.configlib.Configuration;

@Configuration
public class Config {
  public com.nookure.sync.client.config.ClientConfig client = new com.nookure.sync.client.config.ClientConfig();

  @Override
  public String toString() {
    return "Config{" +
        "client=" + client +
        '}';
  }
}
