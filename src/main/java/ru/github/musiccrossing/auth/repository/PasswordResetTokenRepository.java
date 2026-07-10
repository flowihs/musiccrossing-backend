package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.PasswordResetToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUserId(UUID userId);

    List<PasswordResetToken> findByUserId(UUID userId);
}
