package com.felipejaber.talos.presentation.dtos;

public record AuthRequest(
        String email,
        String password
) {
}
