package ru.github.musiccrossing.music.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Test
    void createSuccess() {
        User user = buildDefaultUser();
        Long userId = user.getId();

        Playlist playlist = buildDefaultPlaylist(user);

        PlaylistCreateRequest request = PlaylistCreateRequest.builder()
                .name("My Playlist")
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

        PlaylistResponse response = playlistService.create(request, userId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(playlist.getName(), response.getName());
        Assertions.assertEquals(playlist.getId(), response.getId());
    }

    @Test
    void getAllByUserSuccess() {
        Long userId = 1L;
        User user = buildDefaultUser();

        List<Playlist> expectedPlaylists = List.of(
                buildDefaultPlaylist(user, 10L, "Playlist 1"),
                buildDefaultPlaylist(user, 11L, "Playlist 2"),
                buildDefaultPlaylist(user, 12L, "Playlist 3")
        );

        when(playlistRepository.findByUserId(userId)).thenReturn(expectedPlaylists);

        List<PlaylistResponse> actualResponses = playlistService.getAllByUser(userId);

        Assertions.assertNotNull(actualResponses);
        Assertions.assertEquals(3, actualResponses.size());
        Assertions.assertEquals("Playlist 1", actualResponses.get(0).getName());
        Assertions.assertEquals(10L, actualResponses.get(0).getId());
        Assertions.assertEquals("Playlist 2", actualResponses.get(1).getName());
        Assertions.assertEquals(11L, actualResponses.get(1).getId());
        Assertions.assertEquals("Playlist 3", actualResponses.get(2).getName());
        Assertions.assertEquals(12L, actualResponses.get(2).getId());

        verify(playlistRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getByIdSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        PlaylistResponse response = playlistService.getById(user.getId(), 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("standart", response.getName());

        verify(playlistRepository, times(1)).findById(1L);
    }

    @Test
    void getByIdNotFoundPlaylist() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(PlaylistNotFoundException.class, () -> playlistService.getById(user.getId(), playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void getByIdNotOwnUserPlaylist() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        Assertions.assertThrows(PlaylistNotFoundException.class, () -> playlistService.getById(2L, playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void changePublicStatusPlaylist() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        playlistService.changePublicStatusPlaylist(user.getId(), playlist.getId());

        Assertions.assertTrue(playlist.isPublic());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void changePublicStatusPlaylistNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        Assertions.assertThrows(PlaylistNotOwnedException.class,
                () -> playlistService.changePublicStatusPlaylist(2L, playlist.getId()));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void deleteByIdSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        playlistService.deleteById(user.getId(), playlist.getId());

        verify(playlistRepository, times(1)).findById(1L);
        verify(playlistRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdNotOwn() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        Assertions.assertThrows(PlaylistNotOwnedException.class,
                () -> playlistService.deleteById(2L, playlist.getId()));

        verify(playlistRepository, times(1)).findById(1L);
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

        Assertions.assertEquals(1, playlist.getSounds().size());
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

        Assertions.assertThrows(PlaylistNotOwnedException.class,
                () -> playlistService.addSoundInPlaylist(10L, dto));

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

        Assertions.assertEquals(0, playlist.getSounds().size());

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

        Assertions.assertThrows(PlaylistNotOwnedException.class,
                () -> playlistService.removeSoundInPlaylist(10L, dto));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNameSuccess() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest(playlist.getId(), "new name");

        playlistService.update(user.getId(), dto);

        Assertions.assertEquals("new name", playlist.getName());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNotOwnThrow() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");
        PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest(playlist.getId(), "new name");

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        Assertions.assertThrows(PlaylistNotOwnedException.class,
                () -> playlistService.update(10L, dto));

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    @Test
    void updateNameIsBlank() {
        User user = buildDefaultUser();
        Playlist playlist = buildDefaultPlaylist(user, user.getId(), "standart");
        PlaylistUpdateDataRequest dto = new PlaylistUpdateDataRequest();
        dto.setId(1L);

        when(playlistRepository.findById(playlist.getId())).thenReturn(Optional.of(playlist));

        playlistService.update(user.getId(), dto);

        Assertions.assertEquals("standart", playlist.getName());

        verify(playlistRepository, times(1)).findById(playlist.getId());
    }

    private Playlist buildDefaultPlaylist(User user, Long id, String name) {
        return Playlist.builder()
                .id(id)
                .name(name)
                .user(user)
                .isPublic(false)
                .build();
    }

    private Playlist buildDefaultPlaylist(User user) {
        return Playlist.builder()
                .id(10L)
                .name("My Playlist")
                .user(user)
                .sounds(new ArrayList<>())
                .build();
    }

    private User buildDefaultUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
