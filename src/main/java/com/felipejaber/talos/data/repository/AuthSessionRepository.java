package com.felipejaber.talos.data.repository;

import com.felipejaber.talos.data.entities.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {
    Optional<AuthSession> findByRefreshToken(String refreshToken);
}
