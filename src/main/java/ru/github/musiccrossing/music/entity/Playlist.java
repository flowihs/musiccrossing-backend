package ru.github.musiccrossing.music.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.github.musiccrossing.auth.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    @Id
    private UUID id = UUID.randomUUID();

    @PrePersist
    public void onPrePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    private String name;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private boolean isPublic;

    @Column
    private String avatar;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "playlists_sounds",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "sound_id")
    )
    @Builder.Default
    private List<Sound> sounds = new ArrayList<>();

}
