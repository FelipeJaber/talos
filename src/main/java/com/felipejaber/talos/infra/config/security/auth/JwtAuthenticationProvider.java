package com.felipejaber.talos.infra.config.security.auth;

import com.felipejaber.talos.infra.config.security.InvalidTokenException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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

        String username;
        try {
            username = jwtProvider.getUserNameFromToken(token);
        } catch (Exception e) {
            throw new CredentialsExpiredException("Invalid or expired JWT token", e);
        }

        Set<GrantedAuthority> userAuthorities = jwtProvider.getAuthorities(token);

        return new JwtAuthenticationToken(username, userAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
