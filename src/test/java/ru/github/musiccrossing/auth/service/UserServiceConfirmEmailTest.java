package ru.github.musiccrossing.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.github.musiccrossing.auth.dto.request.GenerateEmailConfirmTokenRequest;
import ru.github.musiccrossing.auth.entity.EmailConfirmToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.exception.auth.TokenExpiredException;
import ru.github.musiccrossing.auth.exception.auth.TokenNotFoundException;
import ru.github.musiccrossing.auth.exception.user.UserNotFoundException;
import ru.github.musiccrossing.auth.repository.EmailConfirmTokenRepository;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceConfirmEmailTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailConfirmTokenRepository emailConfirmTokenRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_USERNAME = "testUser";
    private final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .email(TEST_EMAIL)
                .username(TEST_USERNAME)
                .password("encodedPassword")
                .role(UserRole.USER)
                .enabled(true)
                .enabledMail(false)
                .build();
    }

    private GenerateEmailConfirmTokenRequest createRequest(String email) {
        try {
            Constructor<GenerateEmailConfirmTokenRequest> constructor =
                    GenerateEmailConfirmTokenRequest.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(email);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании GenerateEmailConfirmTokenRequest");
        }
    }

    @Test
    void generateConfirmEmailTokenSuccess() {
        GenerateEmailConfirmTokenRequest request = createRequest(TEST_EMAIL);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(emailConfirmTokenRepository.save(any(EmailConfirmToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.generateConfirmEmailToken(request);

        ArgumentCaptor<EmailConfirmToken> tokenCaptor =
                ArgumentCaptor.forClass(EmailConfirmToken.class);
        verify(emailConfirmTokenRepository).save(tokenCaptor.capture());

        EmailConfirmToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getId());
        assertEquals(TEST_USER_ID, savedToken.getUserId());
        assertNotNull(savedToken.getExpiredAt());
        assertTrue(savedToken.getExpiredAt().after(new Date()));

        verify(mailService).sendConfirmEmail(
                eq(TEST_EMAIL),
                eq(TEST_USERNAME),
                eq(savedToken.getId())
        );
    }

    @Test
    void generateConfirmEmailTokenUserNotFound() {
        GenerateEmailConfirmTokenRequest request = createRequest("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.generateConfirmEmailToken(request));

        verify(emailConfirmTokenRepository, never()).save(any());
        verify(mailService, never()).sendConfirmEmail(any(), any(), any());
    }

    @Test
    void confirmEmailByTokenSuccess() {
        String tokenId = "valid-token-uuid";
        EmailConfirmToken validToken = EmailConfirmToken.builder()
                .id(tokenId)
                .userId(TEST_USER_ID)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .build();

        when(emailConfirmTokenRepository.findById(tokenId))
                .thenReturn(Optional.of(validToken));
        when(userRepository.findById(TEST_USER_ID))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        boolean result = userService.confirmEmailByToken(tokenId);

        assertTrue(result);
        assertTrue(testUser.isEnabledMail());

        verify(userRepository).save(testUser);
        verify(emailConfirmTokenRepository).deleteById(tokenId);
    }

    @Test
    void confirmEmailByTokenTokenNotFound() {
        String invalidTokenId = "invalid-token-uuid";
        when(emailConfirmTokenRepository.findById(invalidTokenId))
                .thenReturn(Optional.empty());

        assertThrows(TokenNotFoundException.class,
                () -> userService.confirmEmailByToken(invalidTokenId));

        verify(userRepository, never()).save(any());
        verify(emailConfirmTokenRepository, never()).deleteById(any());
    }

    @Test
    void confirmEmailByTokenExpired() {
        String tokenId = "expired-token-uuid";
        EmailConfirmToken expiredToken = EmailConfirmToken.builder()
                .id(tokenId)
                .userId(TEST_USER_ID)
                .expiredAt(new Date(System.currentTimeMillis() - 1000 * 60))
                .build();

        when(emailConfirmTokenRepository.findById(tokenId))
                .thenReturn(Optional.of(expiredToken));

        assertThrows(TokenExpiredException.class,
                () -> userService.confirmEmailByToken(tokenId));

        verify(userRepository, never()).save(any());
        verify(emailConfirmTokenRepository, never()).deleteById(any());
    }

    @Test
    void confirmEmailByTokenUserNotFound() {
        String tokenId = "valid-token-uuid";
        EmailConfirmToken validToken = EmailConfirmToken.builder()
                .id(tokenId)
                .userId(999L)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .build();

        when(emailConfirmTokenRepository.findById(tokenId))
                .thenReturn(Optional.of(validToken));
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.confirmEmailByToken(tokenId));

        verify(userRepository, never()).save(any());
        verify(emailConfirmTokenRepository, never()).deleteById(any());
    }

    @Test
    void generateConfirmEmailTokenUniqueTokenFormat() {
        GenerateEmailConfirmTokenRequest request = createRequest(TEST_EMAIL);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<EmailConfirmToken> firstTokenCaptor =
                ArgumentCaptor.forClass(EmailConfirmToken.class);
        ArgumentCaptor<EmailConfirmToken> secondTokenCaptor =
                ArgumentCaptor.forClass(EmailConfirmToken.class);

        userService.generateConfirmEmailToken(request);
        verify(emailConfirmTokenRepository).save(firstTokenCaptor.capture());

        userService.generateConfirmEmailToken(request);
        verify(emailConfirmTokenRepository, times(2)).save(secondTokenCaptor.capture());

        String firstTokenId = firstTokenCaptor.getValue().getId();
        String secondTokenId = secondTokenCaptor.getValue().getId();

        assertNotEquals(firstTokenId, secondTokenId);

        assertDoesNotThrow(() -> java.util.UUID.fromString(firstTokenId));
        assertDoesNotThrow(() -> java.util.UUID.fromString(secondTokenId));
    }

    @Test
    void confirmEmailByTokenEnabledMailFlagSet() {
        String tokenId = "valid-token-uuid";
        User user = User.builder()
                .id(TEST_USER_ID)
                .email(TEST_EMAIL)
                .username(TEST_USERNAME)
                .enabledMail(false)
                .build();

        EmailConfirmToken validToken = EmailConfirmToken.builder()
                .id(tokenId)
                .userId(TEST_USER_ID)
                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .build();

        when(emailConfirmTokenRepository.findById(tokenId))
                .thenReturn(Optional.of(validToken));
        when(userRepository.findById(TEST_USER_ID))
                .thenReturn(Optional.of(user));

        userService.confirmEmailByToken(tokenId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isEnabledMail());
    }
}
