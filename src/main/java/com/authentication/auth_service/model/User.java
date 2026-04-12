package com.authentication.auth_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(
        name = "users",
        indexes = {
                @Index(name = "username_idx", columnList = "username")
        }
)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @Column(unique = true, updatable = false, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;

}
