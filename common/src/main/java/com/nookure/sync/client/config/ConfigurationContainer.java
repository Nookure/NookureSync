package com.nookure.sync.client.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigurationContainer<C> {
  private final AtomicReference<C> configuration = new AtomicReference<>();
  private final Class<C> clazz;
  private final String fileName;
  private final Path path;

  private final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
      .setNameFormatter(NameFormatters.IDENTITY)
      .setFieldFilter(field -> !field.getName().startsWith("$$"))
      .build();

  private ConfigurationContainer(
      Class<C> clazz,
      String fileName,
      Path path
  ) {
    this.clazz = clazz;
    this.fileName = fileName;
    this.path = path;

    C config = YamlConfigurations.update(
        path.resolve(fileName),
        clazz,
        properties
    );

    configuration.set(config);
  }

  public CompletableFuture<Void> reload() {
    return CompletableFuture.runAsync(() -> {
      C config = YamlConfigurations.update(
          path.resolve(fileName),
          clazz,
          properties
      );

      configuration.set(config);
    });
  }

  public CompletableFuture<Void> save() {
    return CompletableFuture.runAsync(() -> {
      C config = configuration.get();

      YamlConfigurations.save(
          path.resolve(fileName),
          clazz,
          config
      );
    });
  }

  public C get() {
    return configuration.get();
  }

  public static <C> ConfigurationContainer<C> load(
      @NotNull final Path path,
      @NotNull final String fileName,
      @NotNull final Class<C> clazz
  ) {
    return new ConfigurationContainer<>(clazz, fileName, path);
  }
}
