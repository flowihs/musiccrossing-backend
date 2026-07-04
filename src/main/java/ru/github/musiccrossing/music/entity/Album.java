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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Album album = (Album) o;
        return id != null && id.equals(album.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
