package ru.github.musiccrossing.music.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.service.UserService;
import ru.github.musiccrossing.music.dto.request.PlaylistCreateRequest;
import ru.github.musiccrossing.music.dto.response.PlaylistResponse;
import ru.github.musiccrossing.music.entity.Playlist;
import ru.github.musiccrossing.music.exception.PlaylistNotFound;
import ru.github.musiccrossing.music.repository.PlaylistRepository;

import java.util.List;

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
    void getAllByUserEmptyListThrowsPlaylistNotFound() {
        Long userId = 1L;

        when(playlistRepository.findByUserId(userId)).thenReturn(List.of());

        Assertions.assertThrows(PlaylistNotFound.class, () -> {
            playlistService.getAllByUser(userId);
        });

        verify(playlistRepository, times(1)).findByUserId(userId);
    }

    private Playlist buildDefaultPlaylist(User user, Long id, String name) {
        return Playlist.builder()
                .id(id)
                .name(name)
                .user(user)
                .build();
    }

    private Playlist buildDefaultPlaylist(User user) {
        return Playlist.builder()
                .id(10L)
                .name("My Playlist")
                .user(user)
                .build();
    }

    private User buildDefaultUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
