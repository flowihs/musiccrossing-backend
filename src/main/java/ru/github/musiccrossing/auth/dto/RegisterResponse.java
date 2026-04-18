package ru.github.musiccrossing.auth.dto;

import lombok.Setter;

@Setter
public class RegisterResponse {
    private Long id;
    private String email;
    private String username;
}
