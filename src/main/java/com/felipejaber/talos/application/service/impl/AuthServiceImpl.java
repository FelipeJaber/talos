package com.felipejaber.talos.application.service.impl;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.mapper.AuthorityMapper;
import com.felipejaber.talos.application.service.AuthService;
import com.felipejaber.talos.data.entities.AuthSessions;
import com.felipejaber.talos.data.entities.UserData;
import com.felipejaber.talos.data.repository.AuthSessionRepository;
import com.felipejaber.talos.data.repository.UserDataRepository;
import com.felipejaber.talos.infra.config.security.auth.JwtProvider;
import com.felipejaber.talos.infra.config.security.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDataRepository userDataRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthServiceImpl(UserDataRepository userDataRepository, AuthSessionRepository sessionRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userDataRepository = userDataRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public AuthenticatedUserData authenticate(String email, String password) {
        UserData emailRelatedData = userDataRepository.findByEmail(email);

        if(!passwordEncoder.matches(password, emailRelatedData.getHashedPassword())) throw new InvalidParameterException("Invalid password");

        String refreshToken = jwtProvider.generateRefreshToken(emailRelatedData.getId());

        AuthSessions authSessions = new AuthSessions();
        authSessions.setUser(emailRelatedData);
        authSessions.setLastUsedAt(Instant.now());
        authSessions.setRefreshToken(refreshToken);

        sessionRepository.save(authSessions);

        String accessToken = jwtProvider.generateToken(emailRelatedData.getId(), AuthorityMapper.toAuthorities(emailRelatedData.getRoles()));

        return new AuthenticatedUserData(
                refreshToken,
                accessToken,
                email,
                emailRelatedData.getId()
        );
    }

    @Override
    public AuthenticatedUserData refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public AuthenticatedUserData setPassword(String token, String password) {
        return null;
    }

    @Override
    public void register(String email) {

    }
}
