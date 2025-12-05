package com.felipejaber.talos.data.repository;

import com.felipejaber.talos.data.entities.RegisterSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegisterSessionRepository extends JpaRepository<RegisterSession, UUID> {
}
