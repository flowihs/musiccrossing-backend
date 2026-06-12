package ru.github.musiccrossing.music.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.github.musiccrossing.music.entity.Playlist;

@Builder
@AllArgsConstructor
public class PlaylistResponse {
    private Long id;
    private String name;

    public static PlaylistResponse fromEntity(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .build();
    }
}
