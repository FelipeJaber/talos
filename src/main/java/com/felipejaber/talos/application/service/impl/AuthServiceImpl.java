package com.felipejaber.talos.application.service.impl;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.mapper.AuthorityMapper;
import com.felipejaber.talos.application.service.AuthService;
import com.felipejaber.talos.data.entities.AuthSession;
import com.felipejaber.talos.data.entities.RegisterSession;
import com.felipejaber.talos.data.entities.UserData;
import com.felipejaber.talos.data.enums.roles;
import com.felipejaber.talos.data.repository.AuthSessionRepository;
import com.felipejaber.talos.data.repository.RegisterSessionRepository;
import com.felipejaber.talos.data.repository.UserDataRepository;
import com.felipejaber.talos.infra.config.security.InvalidTokenException;
import com.felipejaber.talos.infra.config.security.auth.JwtProvider;
import com.felipejaber.talos.infra.config.security.password.PasswordEncoder;
import com.felipejaber.talos.infra.external.smtp.SmtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDataRepository userDataRepository;
    private final AuthSessionRepository sessionRepository;
    private final RegisterSessionRepository registerSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SmtpService smtpService;
    private final String baseUrl;

    @Autowired
    public AuthServiceImpl(UserDataRepository userDataRepository, AuthSessionRepository sessionRepository, RegisterSessionRepository registerSessionRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, SmtpService smtpService,
                           @Value("base.url") String baseUrl) {
        this.userDataRepository = userDataRepository;
        this.sessionRepository = sessionRepository;
        this.registerSessionRepository = registerSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.smtpService = smtpService;
        this.baseUrl = baseUrl;
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
        Optional<RegisterSession> registerSession = registerSessionRepository.findById(UUID.fromString(token));
        if(registerSession.isEmpty()) throw new InvalidTokenException("Invalid token");

        RegisterSession existentSession = registerSession.get();

        String hashedPassword = passwordEncoder.encode(password);

        UserData userData = createNewUser(existentSession.getEmail(), hashedPassword);

        userDataRepository.save(userData);

        String refreshToken = jwtProvider.generateRefreshToken(userData.getId());
        AuthSession newAuthsession = new AuthSession(userData, refreshToken);
        userData.getSessions().add(newAuthsession);

        return new AuthenticatedUserData(
                refreshToken,
                jwtProvider.generateToken(userData.getId(),
                AuthorityMapper.toAuthorities(userData.getRoles())),
                userData.getEmail(),
                userData.getId()
        );
    }

    @Override
    public void register(String email) {

        RegisterSession registerSession = new RegisterSession();

        Optional<RegisterSession> dbRegisterSession = registerSessionRepository.findByEmail(email);

        if(dbRegisterSession.isPresent()) {
            registerSession = dbRegisterSession.get();
        }else{
            registerSession.setEmail(email);
            registerSession.setCreatedAt(Instant.now());
        }

        registerSessionRepository.save(registerSession);

        smtpService.sendEmail(email, "Welcome to Talos!", "Access your account at: " + baseUrl + "/api/auth/v1/set-password/" + registerSession.getSessionId());

    }

    @Override
    public void invalidateSession(String refreshToken) {
        Optional<AuthSession> authSession = sessionRepository.findByRefreshToken(refreshToken);

        if(authSession.isEmpty()) throw new InvalidTokenException("Invalid refresh token");

        AuthSession existentSession = authSession.get();

        if(!jwtProvider.validateToken(refreshToken)) throw new InvalidTokenException("Invalid refresh token");

        if (existentSession.isRevoked()) throw new InvalidTokenException("Revoked refresh token");

        existentSession.setRevoked(true);

        sessionRepository.save(existentSession);
    }


    private UserData createNewUser(String email, String hashedPassword) {
        Set<String> roleList = new HashSet<>();
        roleList.add(roles.USER.name());
        return new UserData(
                email,
                hashedPassword,
                roleList
        );
    }
}
