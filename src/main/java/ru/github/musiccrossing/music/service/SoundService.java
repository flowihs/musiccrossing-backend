package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.music.dto.request.SoundDto;
import ru.github.musiccrossing.music.dto.response.SoundResponseDto;
import ru.github.musiccrossing.music.entity.Album;
import ru.github.musiccrossing.music.entity.Sound;
import ru.github.musiccrossing.music.exception.AccessDeniedException;
import ru.github.musiccrossing.music.exception.AlbumNotFoundException;
import ru.github.musiccrossing.music.repository.AlbumRepository;
import ru.github.musiccrossing.music.repository.SoundRepository;
import ru.github.musiccrossing.storage.FileType;
import ru.github.musiccrossing.storage.service.S3StorageService;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SoundService {
    private final SoundRepository soundRepository;
    private final AlbumRepository albumRepository;
    private final S3StorageService s3StorageService;

    @Transactional
    public SoundResponseDto save(final SoundDto dto, final byte[] data, final FileType fileType, final User user) {
        final Album album = albumRepository.findById(dto.albumId()).orElseThrow(() -> new AlbumNotFoundException("Album not found with id: " + dto.albumId()));

        if (!album.getLoadFromUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User does not have access to this album");
        }

        Sound sound = new Sound();
        sound.setName(dto.name());
        sound.setTrackNumber(nonNull(album.getSounds()) ? album.getSounds().stream().mapToInt(Sound::getTrackNumber).max().orElse(0) + 1 : 1);
        sound.setFileType(fileType);
        sound.setAlbum(album);

        sound = soundRepository.save(sound);

        final String s3Url = s3StorageService.upload(data, sound.getId(), fileType);

        return new SoundResponseDto(
                sound.getId(),
                sound.getName(),
                sound.getTrackNumber(),
                sound.getFileType(),
                s3Url,
                album.getId(),
                album.getAlbumName()
        );
    }
}
