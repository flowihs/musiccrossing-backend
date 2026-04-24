package ru.github.musiccrossing.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.github.musiccrossing.common.error.exception.AuthException;
import ru.github.musiccrossing.common.error.dto.ErrorResponse;
import ru.github.musiccrossing.common.error.exception.MailException;

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
}
