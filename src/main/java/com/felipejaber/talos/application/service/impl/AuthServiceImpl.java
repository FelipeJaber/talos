package com.felipejaber.talos.application.service.impl;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.mapper.AuthorityMapper;
import com.felipejaber.talos.application.service.AuthService;
import com.felipejaber.talos.data.entities.AuthSession;
import com.felipejaber.talos.data.entities.UserData;
import com.felipejaber.talos.data.repository.AuthSessionRepository;
import com.felipejaber.talos.data.repository.UserDataRepository;
import com.felipejaber.talos.infra.config.security.InvalidTokenException;
import com.felipejaber.talos.infra.config.security.auth.JwtProvider;
import com.felipejaber.talos.infra.config.security.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.Optional;

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

        AuthSession authSession = new AuthSession(
                emailRelatedData,
                refreshToken
        );

        sessionRepository.save(authSession);

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

        Optional<AuthSession> authSession = sessionRepository.findByRefreshToken(refreshToken);

        if(authSession.isEmpty()) throw new InvalidTokenException("Invalid refresh token");

        AuthSession existentSession = authSession.get();

        if(!jwtProvider.validateToken(refreshToken)) throw new InvalidTokenException("Invalid refresh token");

        if (existentSession.isRevoked()) throw new InvalidTokenException("Revoked refresh token");

        Instant tokenExpireInstant = jwtProvider.getExpireInstant(refreshToken);

        if(existentSession.isExpired(tokenExpireInstant)) throw new InvalidTokenException("Expired refresh token");

        String accessToken = jwtProvider.generateToken(existentSession.getUser().getId(), AuthorityMapper.toAuthorities(existentSession.getUser().getRoles()));

        return new AuthenticatedUserData(
                null,
                accessToken,
                existentSession.getUser().getEmail(),
                existentSession.getUser().getId()
        );
    }

    @Override
    public AuthenticatedUserData setPassword(String token, String password) {
        return null;
    }

    @Override
    public void register(String email) {

    }
}
