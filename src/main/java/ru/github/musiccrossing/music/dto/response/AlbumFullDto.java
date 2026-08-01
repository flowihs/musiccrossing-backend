package ru.github.musiccrossing.music.dto.response;

import ru.github.musiccrossing.music.entity.Album;
import ru.github.musiccrossing.music.entity.Sound;
import ru.github.musiccrossing.storage.service.S3StorageService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AlbumFullDto(
        UUID id,
        String artistName,
        String albumName,
        boolean publicContent,
        LocalDateTime createdAt,
        List<SoundResponseDto> sounds
) {
    public static AlbumFullDto fromEntity(Album album, S3StorageService s3StorageService) {
        List<SoundResponseDto> soundsList = new ArrayList<>();
        if (album.getSounds() != null) {
            for (Sound sound : album.getSounds()) {
                soundsList.add(new SoundResponseDto(
                        sound.getId(),
                        sound.getName(),
                        sound.getTrackNumber(),
                        sound.getFileType(),
                        s3StorageService.generateUrl(sound.getId(), sound.getFileType()),
                        album.getId(),
                        album.getAlbumName()
                ));
            }
        }
        
        return new AlbumFullDto(
                album.getId(),
                album.getArtistName(),
                album.getAlbumName(),
                album.isPublicContent(),
                album.getLoadFromUser() != null ? album.getLoadFromUser().getCreatedAt() : null,
                soundsList
        );
    }
}
