package ru.github.musiccrossing.music.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.github.musiccrossing.music.entity.Playlist;

@Builder
@Getter
@AllArgsConstructor
public class PlaylistResponse {
    private Long id;
    private String name;
    private boolean isPublic;

    public static PlaylistResponse fromEntity(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .isPublic(playlist.isPublic())
                .build();
    }
}
