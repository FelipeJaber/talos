package com.felipejaber.talos.infra.config.security.auth;

import com.felipejaber.talos.infra.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtService jwtService;

    public String generateToken(UUID userId, Set<GrantedAuthority> authorities) {
        return jwtService.generateToken(userId, authorities);
    }

    public String generateRefreshToken(UUID userId) {
        return jwtService.generateToken(userId);
    }

    public UUID getUserIdFromToken(String token) {
        return jwtService.getUserIdFromToken(token);
    }

    public boolean validateToken(String token) {
        try{
           jwtService.getUserIdFromToken(token);
           return true;
        } catch (Exception e){
            return false;
        }
    }

    public Set<GrantedAuthority> getAuthorities(String token) {
        return jwtService.getAuthorities(token);
    }
}