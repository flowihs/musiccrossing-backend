package ru.github.musiccrossing.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.github.musiccrossing.auth.dto.request.ForgotPasswordRequest;
import ru.github.musiccrossing.auth.dto.request.ResetPasswordRequest;
import ru.github.musiccrossing.auth.entity.PasswordResetToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.exception.auth.HasActiveTokenException;
import ru.github.musiccrossing.auth.exception.auth.PasswordResetTokenNotFound;
import ru.github.musiccrossing.auth.exception.auth.TokenExpiredException;
import ru.github.musiccrossing.auth.repository.PasswordResetTokenRepository;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;
import ru.github.musiccrossing.mail.service.MailTemplateService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServicePasswordResetTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private MailTemplateService mailTemplateService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User storedUser;
    private ForgotPasswordRequest forgotPasswordRequest;

    @BeforeEach
    void setUp() {
        storedUser = User.builder()
                .id(1L)
                .email("test@test.test")
                .username("flowihs")
                .password("$encoded$")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        forgotPasswordRequest = ForgotPasswordRequest.builder()
                .email("test@test.test")
                .build();
    }

    @Test
    void forgotPasswordUserNotFoundShouldReturnWithoutAction() {
        when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
                .thenReturn(Optional.empty());

        userService.forgotPassword(forgotPasswordRequest);

        verify(passwordResetTokenRepository, never()).findByUserId(any());
        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void forgotPasswordUserHasActiveTokenShouldThrowException() {
        PasswordResetToken activeToken = PasswordResetToken.builder()
                .token("active-token")
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();

        when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
                .thenReturn(Optional.of(storedUser));
        when(passwordResetTokenRepository.findByUserId(storedUser.getId()))
                .thenReturn(List.of(activeToken));

        assertThrows(HasActiveTokenException.class, () ->
                userService.forgotPassword(forgotPasswordRequest));

        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void forgotPasswordHasActiveAndExpiredTokensShouldDeleteExpiredAndThrowException() {
        PasswordResetToken activeToken = PasswordResetToken.builder()
                .token("active-token")
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();
        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .token("expired-token")
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .build();

        when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
                .thenReturn(Optional.of(storedUser));
        when(passwordResetTokenRepository.findByUserId(storedUser.getId()))
                .thenReturn(List.of(activeToken, expiredToken));

        assertThrows(HasActiveTokenException.class, () ->
                userService.forgotPassword(forgotPasswordRequest));

        verify(passwordResetTokenRepository).deleteAll(List.of(expiredToken));
        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void forgotPasswordOnlyExpiredTokensShouldCreateNewToken() {
        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .token("expired-token")
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .build();

        when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
                .thenReturn(Optional.of(storedUser));
        when(passwordResetTokenRepository.findByUserId(storedUser.getId()))
                .thenReturn(List.of(expiredToken));

        userService.forgotPassword(forgotPasswordRequest);

        verify(passwordResetTokenRepository, never()).deleteAll(any());

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(storedUser.getId(), savedToken.getUserId());
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getExpiredAt().after(new Date()));

        verify(mailService).sendPasswordResetEmail(
                eq(storedUser.getEmail()),
                eq(savedToken.getToken())
        );
    }

    @Test
    void forgotPasswordNoTokensShouldCreateNewToken() {
        when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
                .thenReturn(Optional.of(storedUser));
        when(passwordResetTokenRepository.findByUserId(storedUser.getId()))
                .thenReturn(Collections.emptyList());

        userService.forgotPassword(forgotPasswordRequest);

        verify(passwordResetTokenRepository, never()).deleteAll(any());

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(storedUser.getId(), savedToken.getUserId());
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getExpiredAt().after(new Date()));

        verify(mailService).sendPasswordResetEmail(
                eq(storedUser.getEmail()),
                eq(savedToken.getToken())
        );
    }

    @Test
    void resetPasswordValidTokenShouldResetPasswordAndDeleteTokens() {
        String token = "valid-token";
        String newPassword = "newPassword123";
        String encodedPassword = "$encoded$newPassword123";

        ResetPasswordRequest resetRequest = ResetPasswordRequest.builder()
                .password(newPassword)
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();

        when(passwordResetTokenRepository.findByToken(token))
                .thenReturn(Optional.of(resetToken));
        when(userRepository.findById(resetToken.getUserId()))
                .thenReturn(Optional.of(storedUser));
        when(passwordEncoder.encode(newPassword))
                .thenReturn(encodedPassword);

        boolean result = userService.resetPassword(token, resetRequest);

        assertTrue(result);
        verify(passwordEncoder).encode(newPassword);
        assertEquals(encodedPassword, storedUser.getPassword());
        verify(userRepository).save(storedUser);
        verify(passwordResetTokenRepository).deleteByUserId(resetToken.getUserId());
    }

    @Test
    void resetPasswordTokenNotFoundShouldThrowException() {
        String token = "non-existent-token";
        ResetPasswordRequest resetRequest = ResetPasswordRequest.builder()
                .password("newPassword123")
                .build();

        when(passwordResetTokenRepository.findByToken(token))
                .thenReturn(Optional.empty());

        assertThrows(PasswordResetTokenNotFound.class, () ->
                userService.resetPassword(token, resetRequest));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).deleteByUserId(any());
    }

    @Test
    void resetPasswordExpiredTokenShouldThrowException() {
        String token = "expired-token";
        ResetPasswordRequest resetRequest = ResetPasswordRequest.builder()
                .password("newPassword123")
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(1L)
                .expiredAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .build();

        when(passwordResetTokenRepository.findByToken(token))
                .thenReturn(Optional.of(resetToken));

        assertThrows(TokenExpiredException.class, () ->
                userService.resetPassword(token, resetRequest));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).deleteByUserId(any());
    }
}