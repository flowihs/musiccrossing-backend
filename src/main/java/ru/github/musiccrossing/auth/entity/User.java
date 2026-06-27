package ru.github.musiccrossing.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.github.musiccrossing.music.entity.Playlist;
import ru.github.musiccrossing.settings.entity.Settings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists;

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
        if (id == null) {
            id = UUID.randomUUID();
        }
        updatedAt = LocalDateTime.now();
    }
}
