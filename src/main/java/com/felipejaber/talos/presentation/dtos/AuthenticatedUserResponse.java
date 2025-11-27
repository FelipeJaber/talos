package com.felipejaber.talos.presentation.dtos;

import java.util.UUID;

public record AuthenticatedUserResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        String email
) {}

