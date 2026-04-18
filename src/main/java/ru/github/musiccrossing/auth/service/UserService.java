package ru.github.musiccrossing.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.entity.PasswordResetToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.entity.EmailConfirmToken;
import ru.github.musiccrossing.auth.exception.*;
import ru.github.musiccrossing.auth.repository.EmailConfirmTokenRepository;
import ru.github.musiccrossing.auth.repository.PasswordResetTokenRepository;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;
    private final EmailConfirmTokenRepository emailConfirmTokenRepository;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .enabled(true)
                .build();

        mailService.sendWelcomeEmail(request.getEmail(), request.getUsername());

        return toResponse(userRepository.save(user));
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getLogin())
                .or(() -> userRepository.findByUsername(request.getLogin()))
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidLoginException();
        }

        return toResponse(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest dto) {
        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());

        if (userOptional.isEmpty()) {
            simulateDelay();
            return;
        }

        User user = userOptional.get();

        List<PasswordResetToken> tokens = passwordResetTokenRepository.findByUserId(user.getId());

        Optional<PasswordResetToken> activeToken = tokens.stream()
                .filter(t -> t.getExpiredAt().after(new Date()))
                .findFirst();

        if (activeToken.isPresent()) {
            List<PasswordResetToken> expiredTokens = tokens.stream()
                    .filter(t -> t.getExpiredAt().before(new Date()))
                    .collect(Collectors.toList());

            if (!expiredTokens.isEmpty()) {
                passwordResetTokenRepository.deleteAll(expiredTokens);
            }

            throw new HasActiveTokenException();
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        mailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public boolean resetPassword(String token, ResetPasswordRequest dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(PasswordResetTokenNotFound::new);


        if (resetToken.getExpiredAt().before(new Date())) {
            throw new TokenExpiredException();
        }

        User user = findById(resetToken.getUserId());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        passwordResetTokenRepository.deleteByUserId(resetToken.getUserId());

        return true;
    }

    public void generateConfirmEmailToken(GenerateEmailConfirmTokenRequest request) {
        String token = UUID.randomUUID().toString();

        User user = findByEmail(request.getEmail());

        EmailConfirmToken emailConfirmToken = EmailConfirmToken.builder()
                .id(token)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .build();

        emailConfirmTokenRepository.save(emailConfirmToken);

        mailService.sendConfirmEmail(user.getEmail(), user.getUsername(), token);
    }

    @Transactional
    public boolean confirmEmailByToken(String token) {
        EmailConfirmToken validToken = emailConfirmTokenRepository.findById(token)
                .orElseThrow(TokenNotFoundException::new);

        if(validToken.getExpiredAt().before(new Date())) {
            throw new TokenExpiredException();
        };

        User user = findById(validToken.getUserId());

        user.setEnabledMail(true);
        userRepository.save(user);

        emailConfirmTokenRepository.deleteById(token);

        return true;
    }

    public boolean isExistingToken(String token) {
        passwordResetTokenRepository.findByToken(token)
                .orElseThrow(PasswordResetTokenNotFound::new);

        return true;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(500 + new Random().nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
