package ru.github.musiccrossing.mail.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.MailException;

public class TemplateNotFoundException extends MailException {
    public TemplateNotFoundException() {
        super("Шаблон письма не был найден", HttpStatus.NOT_FOUND);
    }
}
