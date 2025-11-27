package com.felipejaber.talos.presentation.mapper;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.presentation.dtos.AuthenticatedUserResponse;

public class AuthMapper {

    private AuthMapper() {}

    public static AuthenticatedUserResponse toResponse(AuthenticatedUserData authenticatedUserData) {
        return new AuthenticatedUserResponse(
                authenticatedUserData.accessToken(),
                authenticatedUserData.refreshToken(),
                authenticatedUserData.userId(),
                authenticatedUserData.email()
        );
    }
}
