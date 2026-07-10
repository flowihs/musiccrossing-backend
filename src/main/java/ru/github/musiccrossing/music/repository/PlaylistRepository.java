package ru.github.musiccrossing.music.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Playlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    @EntityGraph(attributePaths = {"sounds"})
    List<Playlist> findByUserId(UUID userId);

    @Override
    @EntityGraph(attributePaths = {"sounds"})
    Optional<Playlist> findById(UUID id);
}
