package ru.github.musiccrossing.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailTemplateService mailTemplateService;

    @Value("${app.base-url}")
    private String baseUrl;

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

        String html = mailTemplateService.render("password-reset", vars);
        sendHtmlEmail(to, "Восстановление пароля", html);
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

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }


}
