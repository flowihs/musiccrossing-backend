package ru.github.musiccrossing.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.github.musiccrossing.settings.entity.Settings;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column()
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean enabledMail;

    @Column()
    private String googleId;

    @Column()
    private boolean registeredWithGoogle;

    @Column()
    private String telegramId;

    @Column()
    private boolean registeredWithTelegram;

    @OneToOne
    @JoinColumn(name = "settings_id")
    private Settings settings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (role == null) {
            role = UserRole.USER;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}