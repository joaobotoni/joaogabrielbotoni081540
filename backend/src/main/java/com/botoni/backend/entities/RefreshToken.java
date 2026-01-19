package com.botoni.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;
}