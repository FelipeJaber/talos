package com.felipejaber.talos.data.repository;

import com.felipejaber.talos.data.entities.AuthSessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSessions, UUID> {
}
