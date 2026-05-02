package ru.github.musiccrossing.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.github.musiccrossing.auth.dto.request.RegisterRequest;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.exception.auth.EmailAlreadyExistsException;
import ru.github.musiccrossing.auth.exception.auth.UsernameAlreadyExistsException;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@test.test")
                .username("flowihs")
                .password("qwerty123")
                .build();
    }

    @Test
    void register() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded123");

        User savedUser = User.builder()
                .id(1L)
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password("encoded123")
                .role(UserRole.USER)
                .enabled(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var response = userService.register(registerRequest);

        assertEquals(1L, response.getId());
        assertEquals("test@test.test", response.getEmail());
        assertEquals("flowihs", response.getUsername());

        verify(mailService).sendWelcomeEmail(registerRequest.getEmail(), registerRequest.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerWhenEmailExistsShouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(registerRequest));
    }

    @Test
    void registerWhenUsernameExistsShouldThrowUsernameAlreadyExistsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(registerRequest));
    }
}
