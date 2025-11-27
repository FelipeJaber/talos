package com.felipejaber.talos.infra.config.security.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;

    @Autowired
    public JwtAuthenticationProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = authentication.getCredentials().toString();

        UUID userId;
        try {
            userId = jwtProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new CredentialsExpiredException("Invalid or expired JWT token", e);
        }

        Set<GrantedAuthority> userAuthorities = jwtProvider.getAuthorities(token);

        return new JwtAuthenticationToken(userId, userAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
