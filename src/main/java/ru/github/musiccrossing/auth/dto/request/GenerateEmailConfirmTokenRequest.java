package ru.github.musiccrossing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GenerateEmailConfirmTokenRequest {
    @NotBlank
    @Email
    private String email;
}
