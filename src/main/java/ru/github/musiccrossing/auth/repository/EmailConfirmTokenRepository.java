package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.EmailConfirmToken;
import ru.github.musiccrossing.auth.entity.PasswordResetToken;

import java.util.List;
import java.util.Optional;

public interface EmailConfirmTokenRepository extends JpaRepository<EmailConfirmToken, String> {
    List<EmailConfirmToken> findByUserId(Long userId);
}
