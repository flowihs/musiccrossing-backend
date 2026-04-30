package ru.github.musiccrossing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RecoverCompromisedAccountRequest {
    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
