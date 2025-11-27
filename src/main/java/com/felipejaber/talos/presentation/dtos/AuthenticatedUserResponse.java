package com.felipejaber.talos.presentation.dtos;

public record AuthenticatedUserResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email
) {}

