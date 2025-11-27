package com.felipejaber.talos.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
public class AuthSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name ="refresh_token", nullable = false, updatable = false)
    private String refreshToken;

    @CreationTimestamp
    @Column(name ="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name ="expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name ="last_used_at", nullable = false)
    private Instant lastUsedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserData user;

    @Column(name ="revoked", nullable = false)
    private boolean revoked;


}
