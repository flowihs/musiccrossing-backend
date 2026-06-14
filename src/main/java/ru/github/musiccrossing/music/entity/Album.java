package ru.github.musiccrossing.music.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.github.musiccrossing.auth.entity.User;

import java.util.List;

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue
    private long id;

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
