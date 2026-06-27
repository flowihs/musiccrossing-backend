package ru.github.musiccrossing.music.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sounds")
@Getter
@Setter
@NoArgsConstructor
public class Sound {

    @Id
    private UUID id;

    @PrePersist
    public void onPrePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToMany(mappedBy = "sounds", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();
}
