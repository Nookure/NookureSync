package com.nookure.sync.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;

@Configuration
public class SSLConfig {
  public SSLConfig(
      @NotNull final String caCertPath,
      @NotNull final String keyPath,
      @NotNull final String certPath
  ) {
    this.caCertPath = caCertPath;
    this.keyPath = keyPath;
    this.certPath = certPath;
  }

  public SSLConfig() {
  }

  @Comment("Whether SSL should be enabled")
  public boolean enabled = true;

  @Comment("Path to the CA certificate, the certificates are on the certificates folder")
  public String caCertPath = "ca.crt";

  @Comment("Path to the private key, the certificates are on the certificates folder")
  public String keyPath = "server.key";

  @Comment("Path to the certificate signed by the CA, the certificates are on the certificates folder")
  public String certPath = "server.crt";
}
