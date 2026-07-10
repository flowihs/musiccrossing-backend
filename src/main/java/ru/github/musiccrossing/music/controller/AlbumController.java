package ru.github.musiccrossing.music.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.security.UserDetailsImpl;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.dto.request.AlbumUpdateRequest;
import ru.github.musiccrossing.music.dto.response.AlbumCreateResponse;
import ru.github.musiccrossing.music.dto.response.AlbumFullDto;
import ru.github.musiccrossing.music.dto.response.AlbumSummaryDto;
import ru.github.musiccrossing.music.service.AlbumService;

import java.util.UUID;

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

    @GetMapping("/{id}")
    public AlbumFullDto getById(@PathVariable("id") final UUID id, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return albumService.getById(id, userDetails.getUser().getId());
    }

    @GetMapping("/by-user")
    public Page<AlbumSummaryDto> getByUser(@AuthenticationPrincipal final UserDetailsImpl userDetails, Pageable pageable) {
        return albumService.getByUser(userDetails.getUser().getId(), pageable);
    }

    @GetMapping("/public")
    public Page<AlbumSummaryDto> getPublic(Pageable pageable) {
        return albumService.getPublic(pageable);
    }

    @PutMapping("/{id}")
    public AlbumFullDto update(@PathVariable("id") final UUID id, @RequestBody @Valid final AlbumUpdateRequest request, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return albumService.update(id, request, userDetails.getUser().getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") final UUID id, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        albumService.deleteById(id, userDetails.getUser().getId());
    }
}
