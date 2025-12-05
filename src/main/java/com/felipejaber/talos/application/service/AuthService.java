package com.felipejaber.talos.application.service;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;

public interface AuthService {
    AuthenticatedUserData authenticate(String email, String password);
    AuthenticatedUserData refreshToken(String refreshToken);
    AuthenticatedUserData setPassword(String token, String password);
    void register(String email);
    void invalidateSession(String refreshToken);
}
