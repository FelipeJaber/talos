package com.felipejaber.talos.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name ="refresh_token", nullable = false, updatable = false)
    private String refreshToken;

    @CreationTimestamp
    @Column(name ="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name ="last_used_at", nullable = false)
    private Instant lastUsedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserData user;

    @Column(name ="revoked", nullable = false)
    private boolean revoked;

    public boolean isExpired(Instant expiresAt){
        return Instant.now().isAfter(expiresAt);
    }

    public AuthSession(UserData user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.lastUsedAt = Instant.now();
        this.revoked = false;
    }

}
