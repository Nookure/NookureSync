package com.nookure.sync;

import org.tinylog.Logger;

public class Bootstrap {
  public static void main(String[] args) {
    Thread.currentThread().setContextClassLoader(Bootstrap.class.getClassLoader());

    Logger.info("Hello, World!");
    Logger.debug("Hello, World!");
  }
}
