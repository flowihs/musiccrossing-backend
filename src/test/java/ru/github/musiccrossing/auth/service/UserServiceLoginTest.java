package ru.github.musiccrossing.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.github.musiccrossing.auth.dto.request.LoginRequest;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.exception.auth.InvalidLoginException;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class  UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    private LoginRequest loginByEmailRequest;
    private LoginRequest loginByUsernameRequest;
    private LoginRequest loginByUsernameWithoutPassword;
    private User storedUserWithoutPassword;
    private User storedUser;

    @BeforeEach
    void setUp() {
        loginByEmailRequest = LoginRequest.builder()
                .login("test@test.test")
                .password("qwerty123")
                .build();

        loginByUsernameRequest = LoginRequest.builder()
                .login("flowihs")
                .password("qwerty123")
                .build();

        loginByUsernameWithoutPassword = LoginRequest.builder()
                .login("flowihs")
                .password(null)
                .build();

        storedUser = User.builder()
                .id(1L)
                .email("test@test.test")
                .username("flowihs")
                .password("$encoded$")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        storedUserWithoutPassword = User.builder()
                .id(2L)
                .email("nopass@test.test")
                .username("nopass")
                .password(null)
                .role(UserRole.USER)
                .enabled(true)
                .build();
    }

    @Test
    void loginWithEmail() {
        when(userRepository.findByEmail(loginByEmailRequest.getLogin())).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(loginByEmailRequest.getPassword(), storedUser.getPassword())).thenReturn(true);

        var response = userService.login(loginByEmailRequest);

        assertEquals(storedUser.getEmail(), response.getEmail());
        assertEquals(storedUser.getUsername(), response.getUsername());
        assertEquals(storedUser.getId(), response.getId());

        verify(userRepository).findByEmail(loginByEmailRequest.getLogin());
    }

    @Test
    void loginWithUsername() {
        when(userRepository.findByUsername(loginByUsernameRequest.getLogin())).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(loginByUsernameRequest.getPassword(), storedUser.getPassword())).thenReturn(true);

        var response = userService.login(loginByUsernameRequest);

        assertEquals(storedUser.getEmail(), response.getEmail());
        assertEquals(storedUser.getUsername(), response.getUsername());
        assertEquals(storedUser.getId(), response.getId());

        verify(userRepository).findByUsername(loginByUsernameRequest.getLogin());

    }

    @Test
    void loginWhenWrongPasswordShouldInvalidLoginException() {
        when(userRepository.findByUsername(loginByUsernameRequest.getLogin())).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(loginByUsernameRequest.getPassword(), storedUser.getPassword())).thenReturn(false);
        assertThrows(InvalidLoginException.class, () -> userService.login(loginByUsernameRequest));
    }

    @Test
    void loginWhenWithoutPasswordShouldInvalidLoginException() {
        when(userRepository.findByEmail(loginByUsernameWithoutPassword.getLogin()))
                .thenReturn(Optional.empty());
        when(userRepository.findByUsername(loginByUsernameWithoutPassword.getLogin()))
                .thenReturn(Optional.of(storedUserWithoutPassword));

        assertThrows(InvalidLoginException.class, () -> userService.login(loginByUsernameWithoutPassword));

        verify(passwordEncoder, never()).matches(anyString(), anyString());

        verify(userRepository).findByEmail(loginByUsernameWithoutPassword.getLogin());
        verify(userRepository).findByUsername(loginByUsernameWithoutPassword.getLogin());
    }
}
