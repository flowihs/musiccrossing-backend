package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.music.repository.SoundRepository;

@Service
@RequiredArgsConstructor
public class SoundService {
    private final SoundRepository soundRepository;

    public void save() {

    }
}
