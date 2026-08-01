package ru.github.musiccrossing.music.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Album;

import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Override
    @EntityGraph(attributePaths = {"sounds"})
    Optional<Album> findById(UUID id);

    @EntityGraph(attributePaths = {"sounds"})
    Page<Album> findByLoadFromUserId(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"sounds"})
    Page<Album> findByPublicContentTrue(Pageable pageable);
}
