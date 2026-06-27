package ru.github.musiccrossing.music.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.music.dto.request.SoundDto;
import ru.github.musiccrossing.music.service.SoundService;

@RestController
@RequestMapping("/sound")
@RequiredArgsConstructor
public class SoundController {
    private final SoundService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final SoundDto soundDto) {

    }
}
