package ru.github.musiccrossing.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.entity.PasswordResetToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.exception.*;
import ru.github.musiccrossing.auth.repository.PasswordResetTokenRepository;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;

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

    public boolean forgotPassword(ForgotPasswordRequest dto) {
        User user = findByEmail(dto.getEmail());

        List<PasswordResetToken> tokens = passwordResetTokenRepository.findByUserId(user.getId());

        Optional<PasswordResetToken> activeToken = tokens.stream()
                .filter(t -> t.getExpiredAt().after(new Date()))
                .findFirst();

        if (activeToken.isPresent()) {
            long diffInMillies = activeToken.get().getExpiredAt().getTime() - System.currentTimeMillis();
            long minutesLeft = diffInMillies / (1000 * 60);

            throw new HasActiveTokenException(String.valueOf(minutesLeft + 1) + "минут(а)");
        }

        if (!tokens.isEmpty()) {
            passwordResetTokenRepository.deleteAll(tokens);
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        mailService.sendPasswordResetEmail(user.getEmail(), token);

        return true;
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

        passwordResetTokenRepository.deleteById(resetToken.getId());

        return true;
    }

//    public boolean generateConfirmEmailToken(GenerateEmailConfirmTokenRequest request) {
//        String token = UUID.randomUUID().toString();
//
//        User user = findByEmail(request.getEmail());
//
//        EmailConfir
//
//        return true;
//    }

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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
