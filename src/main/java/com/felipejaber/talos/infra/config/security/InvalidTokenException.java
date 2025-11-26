package com.felipejaber.talos.infra.config.security;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String msg, Throwable cause) {
        super(msg);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
