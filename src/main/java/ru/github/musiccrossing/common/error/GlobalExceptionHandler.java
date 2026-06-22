package ru.github.musiccrossing.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.github.musiccrossing.common.error.exception.*;
import ru.github.musiccrossing.common.error.dto.ErrorResponse;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthErrors(AuthException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Auth error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleAuthErrors(UserException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "User error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<?> handleMailErrors(MailException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Mail error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(SettingsException.class)
    public ResponseEntity<?> handleSettingErrors(SettingsException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Settings error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(PlaylistException.class)
    public ResponseEntity<?> handlePlaylistErrors(PlaylistException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Playlist error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(SoundException.class)
    public ResponseEntity<?> handlePlaylistErrors(SoundException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Sound error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageErrors(SoundException exception, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                "Storage error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }
}
