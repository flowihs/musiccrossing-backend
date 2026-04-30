package ru.github.musiccrossing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutUserRequest {
    @NotBlank
    private Long userId;
}
