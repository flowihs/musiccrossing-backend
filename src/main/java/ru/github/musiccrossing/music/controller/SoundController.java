package ru.github.musiccrossing.music.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.github.musiccrossing.auth.security.UserDetailsImpl;
import ru.github.musiccrossing.music.dto.request.SoundDto;
import ru.github.musiccrossing.music.dto.response.SoundResponseDto;
import ru.github.musiccrossing.music.service.SoundService;
import ru.github.musiccrossing.storage.FileType;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/sound")
@RequiredArgsConstructor
public class SoundController {
    private final SoundService soundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoundResponseDto create(@RequestBody @Valid final SoundDto soundDto,
                                   @RequestParam("file") final MultipartFile file,
                                   @AuthenticationPrincipal final UserDetailsImpl userDetails) throws IOException {
        return soundService.save(soundDto,
                file.getBytes(),
                FileType.valueOf(Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().indexOf("."))),
                userDetails.getUser());
    }
}
