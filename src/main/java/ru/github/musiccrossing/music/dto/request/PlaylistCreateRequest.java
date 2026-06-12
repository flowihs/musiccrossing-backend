package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistCreateRequest {
    @NotBlank
    private String name;
}
