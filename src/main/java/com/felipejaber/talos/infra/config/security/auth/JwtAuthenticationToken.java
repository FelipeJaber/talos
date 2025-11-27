package com.felipejaber.talos.infra.config.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;
    private final String rawToken;

    // === Para token não autenticado ===
    public JwtAuthenticationToken(String rawToken) {
        super((Collection<? extends GrantedAuthority>) null);
        this.userId = null;
        this.rawToken = rawToken;
        setAuthenticated(false);
    }

    // === Para token autenticado ===
    public JwtAuthenticationToken(UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.rawToken = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
