package com.felipejaber.talos.application.dto;

import java.util.UUID;

public record AuthenticatedUserData(
        String refreshToken,
        String accessToken,
        String email,
        UUID userId
) {
}
