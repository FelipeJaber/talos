package com.felipejaber.talos.infra.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public String generateRefreshToken(UUID userId, Set<GrantedAuthority> authorities){
        try {
            Instant now = Instant.now();
            Algorithm algorithm = Algorithm.HMAC512(secret);

            String roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return JWT.create()
                    .withSubject(userId.toString())
                    .withIssuer("talos")
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusMillis(86400000)))
                    .withClaim("roles", roles) // salva roles como claim
                    .sign(algorithm);

        } catch (IllegalArgumentException | JWTCreationException e) {
            log.error("Error generating JWT token: {}", e.getClass().getSimpleName());
            throw new TokenCreationException("Error generating JWT", e);
        }
    }

    public UUID getUserIdFromToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("talos")
                    .build().
                    verify(token);

            String subject = decodedJWT.getSubject();

            if(subject == null || subject.isBlank()) throw new InvalidTokenException("Token has no subject (sub) claim");

            return UUID.fromString(decodedJWT.getSubject());
        } catch (IllegalArgumentException | JWTVerificationException e) {
            log.warn("Invalid JWT signature: {}", e.getClass().getSimpleName());
            throw new InvalidTokenException("Invalid or expired JWT token", e);
        }
    }

    public Set<GrantedAuthority> getAuthorities(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String rolesString = decodedJWT.getClaim("roles").asString();

            if (rolesString == null || rolesString.isBlank()) return Set.of();

            return Arrays.stream(rolesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("Failed to parse authorities from token: {}", e.getClass().getSimpleName());
            return Set.of();
        }
    }

    public String generateRefreshToken(UUID userId) {
        try {
            Instant now = Instant.now();
            Algorithm algorithm = Algorithm.HMAC512(secret);

            return JWT.create()
                    .withSubject(userId.toString())
                    .withIssuer("talos")
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusMillis(expirationInMillis)))
                    .sign(algorithm);

        } catch (IllegalArgumentException | JWTCreationException e) {
            log.error("Error generating JWT token: {}", e.getClass().getSimpleName());
            throw new TokenCreationException("Error generating JWT", e);
        }
    }

    public Instant getExpireInstant(String refreshToken) {
        return JWT.decode(refreshToken).getExpiresAtAsInstant();
    }
}
