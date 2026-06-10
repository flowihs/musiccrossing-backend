package ru.github.musiccrossing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdatePasswordRequest {
    @NotBlank
    String oldPassword;

    @NotBlank
    String newPassword;

    @NotBlank
    String confirmPassword;
}
