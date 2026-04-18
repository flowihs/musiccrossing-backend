package ru.github.musiccrossing.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailTemplateService {
    private final SpringTemplateEngine engine;

    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return engine.process(templateName, context);
    }
}
