package com.felipejaber.talos.infra.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

@Service
@Slf4j
public class JwtService {

    private final String secret;
    private final long expirationInMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationInMillis
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret is missing or too weak");
        }

        this.secret = secret;
        this.expirationInMillis = expirationInMillis;
    }

    public String generateToken(String username){
        try {
            Instant now = Instant.now();

            Algorithm algorithm = Algorithm.HMAC512(secret);

            return JWT.create()
                            .withSubject(username)
                            .withIssuer("talos")
                            .withIssuedAt(Date.from(now))
                            .withExpiresAt(Date.from(now.plusMillis(expirationInMillis)))
                            .sign(algorithm);

        } catch (IllegalArgumentException | JWTCreationException e) {
            log.error("Error generating JWT token: {}", e.getClass().getSimpleName());
            throw new TokenCreationException("Error generating JWT", e);
        }
    }

    public String getUserNameFromToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("talos")
                    .build().
                    verify(token);

            return decodedJWT.getSubject();
        } catch (IllegalArgumentException | JWTVerificationException e) {
            log.warn("Invalid JWT signature: {}", e.getClass().getSimpleName());
            throw new InvalidTokenException("Invalid or expired JWT token", e);
        }
    }

}
