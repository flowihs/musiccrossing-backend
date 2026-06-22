package ru.github.musiccrossing.music.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.service.JwtService;
import ru.github.musiccrossing.music.dto.request.AddSoundInPlaylistRequest;
import ru.github.musiccrossing.music.dto.request.PlaylistCreateRequest;
import ru.github.musiccrossing.music.dto.request.PlaylistUpdateDataRequest;
import ru.github.musiccrossing.music.dto.request.RemoveSoundInPlaylistRequest;
import ru.github.musiccrossing.music.dto.response.PlaylistResponse;
import ru.github.musiccrossing.music.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final JwtService jwtService;
    private final PlaylistService playlistService;

    @PostMapping("/create")
    public ResponseEntity<PlaylistResponse> create(
            @Valid @ModelAttribute PlaylistCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playlistService.create(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getById(HttpServletRequest httpRequest, @PathVariable("id") Long id) {
        Long userId = getUserId(httpRequest);
        return ResponseEntity.ok(playlistService.getById(userId, id));
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<PlaylistResponse>> getAllByUser(HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        return ResponseEntity.ok(playlistService.getAllByUser(userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(HttpServletRequest httpRequest, @PathVariable("id") Long id) {
        Long userId = getUserId(httpRequest);
        playlistService.deleteById(userId, id);
    }

    @PutMapping("/update")
    public ResponseEntity<PlaylistResponse> update(
            HttpServletRequest httpRequest, @Valid @ModelAttribute PlaylistUpdateDataRequest dto) {
        Long userId = getUserId(httpRequest);
        return ResponseEntity.ok(playlistService.update(userId, dto));
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public void changePublicStatusPlaylist(HttpServletRequest httpRequest, @PathVariable("id") Long id) {
        Long userId = getUserId(httpRequest);
        playlistService.changePublicStatusPlaylist(userId, id);
    }

    @PutMapping("/add-sound")
    @ResponseStatus(HttpStatus.OK)
    public void addSoundInPlaylist(HttpServletRequest httpRequest, @Valid @RequestBody AddSoundInPlaylistRequest dto
    ) {
        Long userId = getUserId(httpRequest);
        playlistService.addSoundInPlaylist(userId, dto);
    }

    @PutMapping("/remove-sound")
    @ResponseStatus(HttpStatus.OK)
    public void removeSoundInPlaylist(
            HttpServletRequest httpRequest, @Valid @RequestBody RemoveSoundInPlaylistRequest dto) {
        Long userId = getUserId(httpRequest);
        playlistService.removeSoundInPlaylist(userId, dto);
    }

    private Long getUserId(HttpServletRequest httpRequest) {
        String accessToken = jwtService.getAccessTokenByCookies(httpRequest.getCookies());
        return jwtService.extractUserId(accessToken);
    }
}
