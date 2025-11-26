package com.felipejaber.talos.infra.config.security.auth;

import com.felipejaber.talos.infra.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtService jwtService;

    public String generateToken(String username, Set<GrantedAuthority> authorities) {
        return jwtService.generateToken(username, authorities);
    }

    public String getUserNameFromToken(String token) {
        return jwtService.getUserNameFromToken(token);
    }

    public boolean validateToken(String token) {
        try{
           jwtService.getUserNameFromToken(token);
           return true;
        } catch (Exception e){
            return false;
        }
    }

    public Set<GrantedAuthority> getAuthorities(String token) {
        return jwtService.getAuthorities(token);
    }
}