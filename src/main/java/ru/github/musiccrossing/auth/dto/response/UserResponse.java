package ru.github.musiccrossing.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.settings.entity.Settings;

@Setter
@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Settings settings;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .settings(user.getSettings())
                .build();
    }
}
