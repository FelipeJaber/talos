package com.felipejaber.talos.infra.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class JwtService {

    private final String SECRET;
    private final long EXPIRATION_IN_MILLIS;

    public JwtService(
            @Value("${jwt.secret}") String SECRET,
            @Value("${jwt.expiration}") long EXPIRATION_IN_MILLIS
    ) {
        this.SECRET = SECRET;
        this.EXPIRATION_IN_MILLIS = EXPIRATION_IN_MILLIS;
    }

    public String generateToken(){
        return "";
    }

    public String getUserNameFromToken(String token){
        return "";
    }

    public Timestamp getTokenExpirationDate(String token){
        return null;
    }

    public boolean isTokenExpired(String token){
        return false;
    }

    public boolean isTokenValid(String token){
        return false;
    }
}
