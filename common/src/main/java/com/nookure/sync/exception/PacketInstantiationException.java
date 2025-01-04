package com.nookure.sync.exception;

public class PacketInstantiationException extends RuntimeException {
  public PacketInstantiationException(String message) {
    super(message);
  }

  public PacketInstantiationException(String message, Throwable cause) {
    super(message, cause);
  }
}
