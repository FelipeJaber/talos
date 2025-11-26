package com.felipejaber.talos.infra.config.security.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String rawToken;

    // === Para token não autenticado ===
    public JwtAuthenticationToken(String rawToken) {
        super((Collection<? extends GrantedAuthority>) null);
        this.username = null;
        this.rawToken = rawToken;
        setAuthenticated(false);
    }

    // === Para token autenticado ===
    public JwtAuthenticationToken(String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.rawToken = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
