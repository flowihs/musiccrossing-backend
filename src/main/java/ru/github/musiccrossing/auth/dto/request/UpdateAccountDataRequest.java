package ru.github.musiccrossing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateAccountDataRequest {
    @NotBlank
    private Long id;

    private String username;
}
