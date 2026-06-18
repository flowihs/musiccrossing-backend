package ru.github.musiccrossing.music.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUpdateDataRequest {
    private Long id;
    private String name;
}
