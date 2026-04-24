package ru.github.musiccrossing.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TelegramLoginRequest {
    @NotBlank
    private String id;

    @Size(max = 32, message = "username не может быть длиннее 32 символов")
    private String username;

    @NotBlank
    private String first_name;

    @NotBlank
    private String auth_date;

    @NotBlank
    private String hash;
}
