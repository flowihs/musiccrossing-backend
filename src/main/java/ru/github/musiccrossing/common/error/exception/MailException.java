package ru.github.musiccrossing.common.error.exception;

import org.springframework.http.HttpStatus;

public class MailException extends RuntimeException {
    private final HttpStatus status;

    public MailException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
