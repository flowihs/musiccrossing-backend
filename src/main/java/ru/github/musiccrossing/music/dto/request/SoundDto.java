package ru.github.musiccrossing.music.dto.request;

import java.util.UUID;

public record SoundDto(UUID albumId, String name) {
}
