package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.github.musiccrossing.music.exception.SoundNotFoundException;
import ru.github.musiccrossing.music.repository.PlaylistRepository;
import ru.github.musiccrossing.music.repository.SoundRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SoundRepository soundRepository;
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
                .map(PlaylistResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PlaylistResponse getById(Long userId, Long playlistId) {
        Playlist playlist = findById(playlistId);

        if (!playlist.getUser().getId().equals(userId) && !playlist.isPublic()) {
            throw new PlaylistNotFoundException();
        }

        return PlaylistResponse.fromEntity(playlist);
    }

    @Transactional
    public void deleteById(Long userId, Long playlistId) {
        Playlist playlist = findById(playlistId);

        if (!playlist.getUser().getId().equals(userId)) {
            throw new PlaylistNotOwnedException();
        }

        playlistRepository.deleteById(playlistId);
    }

    @Transactional
    public PlaylistResponse update(Long userId, PlaylistUpdateDataRequest dto) {
        Playlist playlist = findById(dto.getId());

        if (!playlist.getUser().getId().equals(userId)) {
            throw new PlaylistNotOwnedException();
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            playlist.setName(dto.getName());
        }

        return PlaylistResponse.fromEntity(playlist);
    }

    @Transactional
    public void changePublicStatusPlaylist(Long userId, Long playlistId) {
        Playlist playlist = findById(playlistId);

        if (!playlist.getUser().getId().equals(userId)) {
            throw new PlaylistNotOwnedException();
        }

        playlist.setPublic(!playlist.isPublic());
    }

    @Transactional
    public void addSoundInPlaylist(Long userId, AddSoundInPlaylistRequest dto) {
        Playlist playlist = findById(dto.getPlaylistId());

        if (!playlist.getUser().getId().equals(userId)) {
            throw new PlaylistNotOwnedException();
        }

        Sound sound = soundRepository.findById(dto.getSoundId())
                .orElseThrow(SoundNotFoundException::new);

        playlist.getSounds().add(sound);
    }

    @Transactional
    public void removeSoundInPlaylist(Long userId, RemoveSoundInPlaylistRequest dto) {
        Playlist playlist = findById(dto.getPlaylistId());

        if (!playlist.getUser().getId().equals(userId)) {
            throw new PlaylistNotOwnedException();
        }

        Sound sound = soundRepository.findById(dto.getSoundId())
                .orElseThrow(SoundNotFoundException::new);

        playlist.getSounds().remove(sound);
    }

    private Playlist findById (Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(PlaylistNotFoundException::new);
    }
}

