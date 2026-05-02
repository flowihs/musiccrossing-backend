package ru.github.musiccrossing.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.github.musiccrossing.auth.dto.request.LoginRequest;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.mail.service.MailService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

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
    private User storedUser;

    @BeforeEach
    void setUp() {
        User storedUser = User.builder()
                .id(1L)
                .email("test@test.test")
                .username("flowihs")
                .password("$encoded$")
                .role(UserRole.USER)
                .enabled(true)
                .build();
    }
}
