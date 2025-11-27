package com.felipejaber.talos.application.service.impl;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.service.AuthService;

public class AuthServiceImpl implements AuthService {
    @Override
    public AuthenticatedUserData authenticate(String email, String password) {
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
