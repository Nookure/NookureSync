package com.nookure.sync.exception;

public class PacketWithoutDefaultConstructorException extends RuntimeException {
  public PacketWithoutDefaultConstructorException(String message) {
    super(message);
  }

  public PacketWithoutDefaultConstructorException(String message, Throwable cause) {
    super(message, cause);
  }
}
