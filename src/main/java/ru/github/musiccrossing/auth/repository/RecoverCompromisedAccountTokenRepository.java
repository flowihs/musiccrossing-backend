package ru.github.musiccrossing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.github.musiccrossing.auth.entity.RecoverCompromisedAccountToken;

public interface RecoverCompromisedAccountTokenRepository extends JpaRepository<RecoverCompromisedAccountToken, String> {
}
