package ru.github.musiccrossing.music.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.github.musiccrossing.storage.FileType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Sound {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer trackNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToMany(mappedBy = "sounds")
    private List<Playlist> playlists = new ArrayList<>();
}
