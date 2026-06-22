package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"playlists"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"playlists"})
    Optional<User> findByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"playlists"})
    Optional<User> findById(Long id);

    Optional<User> findByTelegramId(String telegramId);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
