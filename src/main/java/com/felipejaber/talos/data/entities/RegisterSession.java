package com.felipejaber.talos.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class RegisterSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @Column(unique = true, nullable = false)
    private String email;

    @Autowired
    @CreationTimestamp
    private Instant createdAt;

}
