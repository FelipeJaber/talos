package com.felipejaber.talos.application.service.impl;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.service.AuthService;
import com.felipejaber.talos.data.entities.UserData;
import com.felipejaber.talos.data.repository.AuthSessionRepository;
import com.felipejaber.talos.data.repository.UserDataRepository;
import com.felipejaber.talos.infra.config.security.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDataRepository userDataRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserDataRepository userDataRepository, AuthSessionRepository sessionRepository, PasswordEncoder passwordEncoder) {
        this.userDataRepository = userDataRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticatedUserData authenticate(String email, String password) {
        UserData emailRelatedData = userDataRepository.findByEmail(email);

        if(!passwordEncoder.matches(password, emailRelatedData.getHashedPassword())) throw new InvalidParameterException("Invalid password");



        return null;
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
