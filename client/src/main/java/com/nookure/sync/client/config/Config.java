package com.nookure.sync.client.config;

import com.nookure.sync.config.SSLConfig;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class Config {
  @Comment("Client configuration.")
  public ClientConfig client = new ClientConfig();

  @Comment("""
                    ⚠ WARNING READ THIS CAREFULLY ⚠
      This configuration is almost mandatory, unless you want to backdoor
      your server.
      
      If you don't know what you're doing, please I ENCOURAGE you to learn
      about SSL and how it works or ask a professional before disabling it.

      The server will not be able to establish a secure connection and
      authenticate the client without a valid SSL configuration.
      
      If you disable SSL, anyone can connect to your server and pretend
      to be your client. This is a huge security risk and should be avoided
      at all costs.
      
      The server authenticates the clients by verifying that the client &
      server certificates are signed by the same Certificate Authority (CA).
      
      By the way, even if you enable SSL, I extremely recommend you to
      configure a firewall to only allow connections to the server by
      trusted clients.
                    ⚠ WARNING READ THIS CAREFULLY ⚠
      """)
  public SSLConfig ssl = new SSLConfig(
      "ca.crt",
      "client.key",
      "client.crt"
  );
}
