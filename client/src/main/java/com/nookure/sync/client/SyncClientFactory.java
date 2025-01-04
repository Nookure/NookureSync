package com.nookure.sync.client;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SyncClientFactory {
  /**
   * Create a new SyncClient
   *
   * @param listeners List of listeners to add to the SyncClient
   * @return a new SyncClient
   */
  @NotNull
  SyncClient createSyncClient(@NotNull final List<Object> listeners);
}
