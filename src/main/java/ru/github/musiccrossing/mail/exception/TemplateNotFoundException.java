package ru.github.musiccrossing.mail.exception;

import org.springframework.http.HttpStatus;

public class TemplateNotFoundException extends MailException {
    public TemplateNotFoundException() {
        super("Шаблон письма не был найден", HttpStatus.NOT_FOUND);
    }
}
