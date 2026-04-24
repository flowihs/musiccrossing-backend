package ru.github.musiccrossing.common.error.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
   LocalDateTime timestamp,
   int status,
   String error,
   String messages,
   String path
) {}
