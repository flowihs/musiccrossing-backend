package ru.github.musiccrossing.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GoogleAuthRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String googleId;

    @NotBlank
    private String username;

    @NotBlank
    private String idToken;
}
