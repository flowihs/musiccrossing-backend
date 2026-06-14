package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.service.UserService;
import ru.github.musiccrossing.music.dto.request.PlaylistCreateRequest;
import ru.github.musiccrossing.music.dto.response.PlaylistResponse;
import ru.github.musiccrossing.music.entity.Playlist;
import ru.github.musiccrossing.music.exception.PlaylistNotFound;
import ru.github.musiccrossing.music.repository.PlaylistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserService userService;

    @Transactional
    public PlaylistResponse create(PlaylistCreateRequest request, Long userId) {
        User user = userService.findById(userId);

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .user(user)
                .build();

        return PlaylistResponse.fromEntity(playlistRepository.save(playlist));
    }

    public List<PlaylistResponse> getAllByUser(Long userId) {
        List<Playlist> playlists = playlistRepository.findByUserId(userId);

        return playlists.stream()
                .map(playlist -> PlaylistResponse.builder()
                        .id(playlist.getId())
                        .name(playlist.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
