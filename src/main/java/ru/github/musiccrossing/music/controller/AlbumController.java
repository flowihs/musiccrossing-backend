package ru.github.musiccrossing.music.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.security.UserDetailsImpl;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.dto.response.AlbumCreateResponse;
import ru.github.musiccrossing.music.service.AlbumService;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumCreateResponse create(@RequestBody @Valid final CreateAlbumRequest request, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return albumService.create(request, userDetails.getUser());
    }
}
