package ru.github.musiccrossing.music.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.service.JwtService;
import ru.github.musiccrossing.music.dto.request.PlaylistCreateRequest;
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
    public ResponseEntity<PlaylistResponse> create(@Valid @RequestBody PlaylistCreateRequest request,
                                 HttpServletRequest httpRequest) {
        String accessToken = jwtService.getAccessTokenByCookies(httpRequest.getCookies());
        Long userId = jwtService.extractUserId(accessToken);

        return ResponseEntity
                .status(201)
                .body(playlistService.create(request, userId));
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<PlaylistResponse>> getAllByUser(HttpServletRequest httpRequest) {
        String accessToken = jwtService.getAccessTokenByCookies(httpRequest.getCookies());
        Long userId = jwtService.extractUserId(accessToken);

        return ResponseEntity.ok(playlistService.getAllByUser(userId));
    }
}
