package ru.github.musiccrossing.music.dto.response;

import ru.github.musiccrossing.music.entity.Album;

import java.util.UUID;

public record AlbumSummaryDto(
        UUID id,
        String artistName,
        String albumName,
        boolean publicContent,
        int soundsCount
) {
    public static AlbumSummaryDto fromEntity(Album album) {
        int soundsCount = album.getSounds() != null ? album.getSounds().size() : 0;
        
        return new AlbumSummaryDto(
                album.getId(),
                album.getArtistName(),
                album.getAlbumName(),
                album.isPublicContent(),
                soundsCount
        );
    }
}
