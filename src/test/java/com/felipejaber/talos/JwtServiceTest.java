package com.felipejaber.talos;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.felipejaber.talos.infra.config.security.InvalidTokenException;
import com.felipejaber.talos.infra.config.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("a-very-long-test-secret-value-1234567890", 60000);
    }

    @Test
    void shouldGenerateValidToken(){
        String token = jwtService.generateToken("test-user", Collections.emptySet());

        assertNotNull(token);

        String username = jwtService.getUserNameFromToken(token);

        assertEquals("test-user", username);
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

        assertThrows(InvalidTokenException.class,() -> jwtService.getUserNameFromToken(invalidToken));
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        JwtService shortLived = new JwtService("a-very-long-test-secret-value-1234567890", 1); // 1ms

        String token = shortLived.generateToken("user",Collections.emptySet());

        Thread.sleep(5);

        assertThrows(InvalidTokenException.class,
                () -> shortLived.getUserNameFromToken(token)
        );
    }

    @Test
    void shouldRejectTokenWithDifferentSecret() {
        JwtService other = new JwtService("another-very-long-secret-1234567890", 60000);

        String token = other.generateToken("user",Collections.emptySet());

        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserNameFromToken(token)
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
                () -> jwtService.getUserNameFromToken(token)
        );
    }

    @Test
    void shouldRejectTokenWithoutSubject() {
        Algorithm alg = Algorithm.HMAC512("a-very-long-test-secret-value-1234567890");

        String token = JWT.create()
                .withIssuer("talos")
                .sign(alg);

        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserNameFromToken(token)
        );
    }

    @Test
    void shouldRejectNullToken() {
        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserNameFromToken(null)
        );
    }

    @Test
    void shouldRejectEmptyToken() {
        assertThrows(InvalidTokenException.class,
                () -> jwtService.getUserNameFromToken("")
        );
    }



}
