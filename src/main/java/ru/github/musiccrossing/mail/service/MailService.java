package ru.github.musiccrossing.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.mail.exception.TemplateNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailTemplateService mailTemplateService;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendConfirmEmail(String to, String username, String token) {
        String url = baseUrl + "user/confirm-email?token=" + token;
        Map<String, Object> vars = new HashMap<>();

        vars.put("username", username);
        vars.put("token", token);
        vars.put("confirm_link", url);

        String html = mailTemplateService.render("email-confirmation", vars);

        sendHtmlEmail(to, "Подтверждение почты", html);
    }

    public void sendWelcomeEmail(String to, String username) {
        String url = baseUrl + "auth/login";
        Map<String, Object> vars = new HashMap<>();

        vars.put("username", username);
        vars.put("login_link", url);

        String html = mailTemplateService.render("welcome", vars);

        sendHtmlEmail(to, "Добро пожаловать!", html);
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        String url = baseUrl + "user/reset-password?token=" + resetToken;

        Map<String, Object> vars = new HashMap<>();

        vars.put("reset_link", url);

        String html = getRenderHtml("password-reset", vars);

        sendHtmlEmail(to, "Восстановление пароля", html);
    }

    // Нужно написать шаблон для него
    public void sendUpdatePasswordEmail(String to, String token) {
        String url = baseUrl + "user/recover-compromised-account";

        Map<String, Object> vars = new HashMap<>();

        vars.put("link", url);

        String html = getRenderHtml("update-password", vars);

        sendHtmlEmail(to, "Обновление пароля", html);
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    private String getRenderHtml(String nameTemplate, Map<String, Object> vars) {
        String html = mailTemplateService.render(nameTemplate, vars);

        if (html.isEmpty()) {
            throw new TemplateNotFoundException();
        }

        return html;
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (MessagingException exception) {
            throw new RuntimeException("Ошибка отправки письма", exception);
        }
    }
}
