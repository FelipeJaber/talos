package com.felipejaber.talos.infra.config.security;

public class TokenCreationException extends RuntimeException {
    public TokenCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

