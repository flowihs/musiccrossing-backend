package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaylistCreateRequest {
    @NotBlank
    private String name;
}
