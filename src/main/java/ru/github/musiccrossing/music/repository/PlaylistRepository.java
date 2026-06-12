package ru.github.musiccrossing.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Playlist;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);
}
