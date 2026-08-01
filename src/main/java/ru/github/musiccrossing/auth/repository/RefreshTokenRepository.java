package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUserId(UUID userId);
}
