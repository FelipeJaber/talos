package com.felipejaber.talos.application.mapper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthorityMapper {

    private AuthorityMapper() {}

    public static Set<GrantedAuthority> toAuthorities(Set<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
