package ru.github.musiccrossing.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.music.entity.Sound;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<Sound, UUID> {
}
