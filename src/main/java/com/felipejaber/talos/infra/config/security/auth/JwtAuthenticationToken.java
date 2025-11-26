package com.felipejaber.talos.infra.config.security.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String rawToken;

    // === Construtor para a requisição ENTRANTE (não autenticado) ===
    public JwtAuthenticationToken(String rawToken) {
        super((Collection<? extends GrantedAuthority>) null);
        this.rawToken = rawToken;
        this.principal = null;
        setAuthenticated(false);
    }

    // === Construtor para o usuário AUTENTICADO ===
    public JwtAuthenticationToken(UserDetails user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
