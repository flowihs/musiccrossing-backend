package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.EmailConfirmToken;

import java.util.List;

public interface EmailConfirmTokenRepository extends JpaRepository<EmailConfirmToken, String> {
    List<EmailConfirmToken> findByUserId(Long userId);
}
