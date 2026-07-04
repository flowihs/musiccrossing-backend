package ru.github.musiccrossing.music.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SoundServiceTest {
    @Mock
    private SoundRepository soundRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private SoundService soundService;

    private final User currentUser = User.builder().id(UUID.randomUUID()).build();

    @Test
    void saveWithoutAlbum() {
        final UUID albumId = UUID.randomUUID();
        doReturn(Optional.empty()).when(albumRepository).findById(albumId);

        assertThatThrownBy(() -> soundService.save(new SoundDto(albumId, "test"), new byte[0], FileType.MP3, currentUser))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Album not found with id: " + albumId);
    }

    @Test
    void saveToOtherAlbum() {
        final UUID albumId = UUID.randomUUID();
        doReturn(Optional.of(Album.builder().loadFromUser(User.builder().id(UUID.randomUUID()).build()).build())).when(albumRepository).findById(albumId);

        assertThatThrownBy(() -> soundService.save(new SoundDto(albumId, "test"), new byte[0], FileType.MP3, currentUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User does not have access to this album");
    }

    @Test
    void saveFirstSound() {
        final UUID albumId = UUID.randomUUID();
        final Album album = Album.builder().loadFromUser(User.builder().id(currentUser.getId()).build()).build();
        doReturn(Optional.of(album)).when(albumRepository).findById(albumId);
        final UUID soundId = UUID.randomUUID();
        doReturn(new Sound(soundId, "test", 1, FileType.FLAC, album, List.of())).when(soundRepository)
                .save(new Sound(null, "test", 1, FileType.FLAC, album, List.of()));
        doReturn("s3Url").when(s3StorageService).upload(new byte[]{1, 2, 3, 4, 5, 6}, soundId, FileType.FLAC);

        assertThat(soundService.save(new SoundDto(albumId, "test"), new byte[]{1, 2, 3, 4, 5, 6}, FileType.FLAC, currentUser)).extracting(SoundResponseDto::id)
                .isEqualTo(soundId);
    }
}
