package com.samurai74.minimalblog.domain.entities;

import com.samurai74.minimalblog.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts=new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "reset_token", unique = true)
    private String resetToken;
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiresAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(name, user.name) &&  Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(createdAt);
        return result;
    }
    @PrePersist
    protected  void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
