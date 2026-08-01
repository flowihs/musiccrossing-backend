package ru.github.musiccrossing.music.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.service.UserService;
import ru.github.musiccrossing.music.dto.request.AddSoundInPlaylistRequest;
import ru.github.musiccrossing.music.dto.request.PlaylistCreateRequest;
import ru.github.musiccrossing.music.dto.request.PlaylistUpdateDataRequest;
import ru.github.musiccrossing.music.dto.request.RemoveSoundInPlaylistRequest;
import ru.github.musiccrossing.music.dto.response.PlaylistResponse;
import ru.github.musiccrossing.music.entity.Playlist;
import ru.github.musiccrossing.music.entity.Sound;
import ru.github.musiccrossing.music.exception.PlaylistNotFoundException;
import ru.github.musiccrossing.music.exception.PlaylistNotOwnedException;
import ru.github.musiccrossing.music.repository.PlaylistRepository;
import ru.github.musiccrossing.music.repository.SoundRepository;
import ru.github.musiccrossing.storage.service.ImageConvertService;
import ru.github.musiccrossing.storage.service.StorageService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private SoundRepository soundRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @Mock
    private ImageConvertService imageConvertService;

    @Mock
    private StorageService storageService;


    @Test
    @SneakyThrows
    void createSuccess() {
        final User user = buildDefaultUser();
        final UUID userId = user.getId();
        final Playlist playlist = buildDefaultPlaylist(user);
        final PlaylistCreateRequest request = PlaylistCreateRequest.builder()
                .avatar(new MockMultipartFile("avatar", new ByteArrayInputStream(new byte[]{10, 20, 30})))
                .name("My Playlist")
                .build();
        when(userService.findById(userId)).thenReturn(user);
        when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);
        when(imageConvertService.convertToWebp(any())).thenReturn(new byte[]{1, 2, 3});
        when(storageService.upload(any(), any(), any())).thenReturn("http://test-url");

        final PlaylistResponse response = playlistService.create(request, userId);

        assertNotNull(response);
        assertEquals(playlist.getName(), response.getName());
        assertEquals(playlist.getId(), response.getId());
    }

    @Test
    void getAllByUserSuccess() {
        final UUID userId = UUID.randomUUID();
        User user = buildDefaultUser();

        List<Playlist> expectedPlaylists = List.of(
                buildDefaultPlaylist(user, UUID.randomUUID(), "Playlist 1"),
                buildDefaultPlaylist(user, UUID.randomUUID(), "Playlist 2"),
                buildDefaultPlaylist(user, UUID.randomUUID(), "Playlist 3"));

        when(playlistRepository.findByUserId(userId)).thenReturn(expectedPlaylists);

        List<PlaylistResponse> actualResponses = playlistService.getAllByUser(userId);

        assertNotNull(actualResponses);
        assertEquals(3, actualResponses.size());
        assertEquals("Playlist 1", actualResponses.get(0).getName());
        assertEquals(expectedPlaylists.get(0).getId(), actualResponses.get(0).getId());
        assertEquals("Playlist 2", actualResponses.get(1).getName());
        assertEquals(expectedPlaylists.get(1).getId(), actualResponses.get(1).getId());
        assertEquals("Playlist 3", actualResponses.get(2).getName());
        assertEquals(expectedPlaylists.get(2).getId(), actualResponses.get(2).getId());

        verify(playlistRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getByIdSuccess() {
        final User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        PlaylistResponse response = playlistService.getById(user.getId(), user.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals("standart", response.getName());

        verify(playlistRepository, times(1)).findById(user.getId());
    }

    @Test
    void getByIdNotFoundPlaylist() {
        final User user = buildDefaultUser();
        final Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(PlaylistNotFoundException.class, () -> playlistService.getById(user.getId(), playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void getByIdNotOwnUserPlaylist() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotFoundException.class, () -> playlistService.getById(UUID.randomUUID(), playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void changePublicStatusPlaylist() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        playlistService.changePublicStatusPlaylist(user.getId(), playlist.getId());

        Assertions.assertTrue(playlist.isPublic());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void changePublicStatusPlaylistNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotOwnedException.class, () -> playlistService.changePublicStatusPlaylist(UUID.randomUUID(), playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void deleteByIdSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        playlistService.deleteById(user.getId(), playlist.getId());

        verify(playlistRepository, times(1)).findById(user.getId());
        verify(playlistRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void deleteByIdNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(user.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotOwnedException.class, () -> playlistService.deleteById(UUID.randomUUID(), playlist.getId()));

        verify(playlistRepository, times(1)).findById(user.getId());
    }

    @Test
    void addSoundInPlaylistSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        UUID soundId = UUID.randomUUID();

        Sound sound = new Sound();
        sound.setId(soundId);

        AddSoundInPlaylistRequest dto = new AddSoundInPlaylistRequest(playlist.getId(), soundId);

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));
        when(soundRepository.findById(soundId)).thenReturn(Optional.of(sound));

        playlistService.addSoundInPlaylist(user.getId(), dto);

        assertEquals(1, playlist.getSounds().size());
        Assertions.assertTrue(playlist.getSounds().contains(sound));

        verify(playlistRepository, times(1)).findById(playlist.getId());
        verify(soundRepository, times(1)).findById(soundId);
    }

    @Test
    void addSoundInPlaylistNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        UUID soundId = UUID.randomUUID();

        AddSoundInPlaylistRequest dto = new AddSoundInPlaylistRequest(playlist.getId(), soundId);

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotOwnedException.class, () -> playlistService.addSoundInPlaylist(UUID.randomUUID(), dto));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void removeSoundInPlaylistSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        UUID soundId = UUID.randomUUID();

        Sound sound = new Sound();
        sound.setId(soundId);

        playlist.getSounds().add(sound);

        RemoveSoundInPlaylistRequest dto = new RemoveSoundInPlaylistRequest(playlist.getId(), soundId);

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));
        when(soundRepository.findById(soundId)).thenReturn(Optional.of(sound));

        playlistService.removeSoundInPlaylist(user.getId(), dto);

        assertEquals(0, playlist.getSounds().size());

        verify(playlistRepository, times(1)).findById(playlist.getId());
        verify(soundRepository, times(1)).findById(soundId);
    }

    @Test
    void removeSoundInPlaylistNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        UUID soundId = UUID.randomUUID();
        RemoveSoundInPlaylistRequest dto = new RemoveSoundInPlaylistRequest(playlist.getId(), soundId);

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotOwnedException.class, () -> playlistService.removeSoundInPlaylist(UUID.randomUUID(), dto));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNameSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest();
        dto.setName("new name");
        dto.setId(playlist.getId());

        playlistService.update(user.getId(), dto);

        assertEquals("new name", playlist.getName());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNotOwnThrow() {
        final User user = buildDefaultUser();
        final Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");
        final PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest();
        dto.setId(playlist.getId());
        dto.setName("new name");

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        assertThrows(PlaylistNotOwnedException.class, () -> playlistService.update(UUID.randomUUID(), dto));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNameIsBlank() {
        final User user = buildDefaultUser();
        final Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");
        final PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest();
        dto.setId(playlist.getId());

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        playlistService.update(user.getId(), dto);

        assertEquals("standart", playlist.getName());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    private Playlist buildDefaultPlaylist(User user, UUID id, String name) {
        return Playlist.builder()
                .id(id)
                .name(name)
                .user(user)
                .isPublic(false)
                .build();
    }

    private Playlist buildDefaultPlaylist(User user) {
        return Playlist.builder()
                .id(UUID.randomUUID())
                .name("My Playlist")
                .user(user)
                .sounds(new ArrayList<>())
                .build();
    }

    private User buildDefaultUser() {
        final UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        return user;
    }
}
