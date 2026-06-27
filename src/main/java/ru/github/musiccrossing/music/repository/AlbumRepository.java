package ru.github.musiccrossing.music.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Album;

import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Override
    @EntityGraph(attributePaths = {"sounds"})
    Optional<Album> findById(UUID id);
}
