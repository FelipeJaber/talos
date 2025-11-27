package com.felipejaber.talos;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.felipejaber.talos.infra.config.security.InvalidTokenException;
import com.felipejaber.talos.infra.config.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("a-very-long-test-secret-value-1234567890", 60000);
    }

    @Test
    void shouldGenerateValidToken(){
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateRefreshToken(userId, Collections.emptySet());

        assertNotNull(token);

        UUID userIdFromToken = jwtService.getUserIdFromToken(token);

        assertEquals(userId, userIdFromToken);
    }

    @Test
    void shouldRejectWeakSecret() {
        //The secret length must be at least 32 characters
        assertThrows(IllegalStateException.class,
                () -> new JwtService("weak-secret", 60000)
        );
    }

    @Test
    void shouldRejectInvalidToken(){
        String invalidToken = "a-very-long-but-invalid-test-secret-value-1234567890";

        assertThrows(InvalidTokenException.class,() -> jwtService.getUserIdFromToken(invalidToken));
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        UUID userId = UUID.randomUUID();
        JwtService shortLived = new JwtService("a-very-long-test-secret-value-1234567890", 1); // 1ms

        String token = shortLived.generateRefreshToken(userId,Collections.emptySet());

        Thread.sleep(5);

        assertThrows(InvalidTokenException.class,
                () -> shortLived.getUserIdFromToken(token)
        );
    }

    @Test
    void shouldRejectTokenWithDifferentSecret() {
        UUID userId = UUID.randomUUID();
        JwtService other = new JwtService("another-very-long-secret-1234567890", 60000);

        String token = other.generateRefreshToken(userId,Collections.emptySet());

        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserIdFromToken(token)
        );
    }

    @Test
    void shouldRejectTokenWithWrongIssuer() {
        Algorithm alg = Algorithm.HMAC512("a-very-long-test-secret-value-1234567890");

        String token = JWT.create()
                .withSubject("user")
                .withIssuer("wrong-issuer")
                .sign(alg);

        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserIdFromToken(token)
        );
    }

    @Test
    void shouldRejectTokenWithoutSubject() {
        Algorithm alg = Algorithm.HMAC512("a-very-long-test-secret-value-1234567890");

        String token = JWT.create()
                .withIssuer("talos")
                .sign(alg);

        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserIdFromToken(token)
        );
    }

    @Test
    void shouldRejectNullToken() {
        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserIdFromToken(null)
        );
    }

    @Test
    void shouldRejectEmptyToken() {
        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserIdFromToken("")
        );
    }



}
