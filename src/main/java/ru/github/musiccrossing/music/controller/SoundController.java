package ru.github.musiccrossing.music.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.music.dto.request.SoundDto;

@RestController
@RequestMapping("/sound")
@RequiredArgsConstructor
public class SoundController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final SoundDto soundDto) {

    }
}
