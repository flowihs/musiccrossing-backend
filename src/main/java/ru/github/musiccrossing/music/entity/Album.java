package ru.github.musiccrossing.music.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.github.musiccrossing.auth.entity.User;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue
    private UUID id;

    @PrePersist
    public void onPrePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false)
    private String albumName;

    @OneToMany(mappedBy = "album", orphanRemoval = true)
    private List<Sound> sounds;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User loadFromUser;

    private boolean publicContent;
}
