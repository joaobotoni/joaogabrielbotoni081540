package com.botoni.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    @Override
    public @NonNull String getUsername() {
        return this.email;
    }

    public String getName() {
        return this.username;
    }
}