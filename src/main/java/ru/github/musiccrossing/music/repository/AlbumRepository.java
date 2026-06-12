package ru.github.musiccrossing.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
