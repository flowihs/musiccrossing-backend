package ru.github.musiccrossing.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}
