package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUserId(Long userId);
}
