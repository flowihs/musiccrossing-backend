//package ru.github.musiccrossing.auth.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import ru.github.musiccrossing.auth.dto.request.LoginRequest;
////import ru.github.musiccrossing.auth.entity.PasswordResetToken;
////import ru.github.musiccrossing.auth.entity.User;
////import ru.github.musiccrossing.auth.entity.UserRole;
////import ru.github.musiccrossing.auth.repository.PasswordResetTokenRepository;
////import ru.github.musiccrossing.auth.repository.UserRepository;
////import ru.github.musiccrossing.mail.service.MailService;
////import ru.github.musiccrossing.mail.service.MailTemplateService;
////
////import java.util.Date;
//
//@ExtendWith(MockitoExtension.class)
//@Disabled("Тесты временно отключены")
//public class UserServiceTest {
////
////    @Mock
////    private UserRepository userRepository;
////
////    @Mock
////    private PasswordEncoder passwordEncoder;
////
////    @Mock
////    private MailTemplateService mailTemplateService;
////
////    @Mock
////    private PasswordResetTokenRepository passwordResetTokenRepository;
////
////    @Mock
////    private MailService mailService;
////
////    @InjectMocks
////    private UserService userService;
////
////    private LoginRequest loginByEmailRequest;
////    private LoginRequest loginByUsernameRequest;
////    private User storedUser;
////    private PasswordResetToken passwordResetToken;
////
////    @BeforeEach
////    void setUp() {
////        storedUser = User.builder()
////            .id(1L)
////            .email("test@test.test")
////            .username("flowihs")
////            .password("$encoded$")
////            .role(UserRole.USER)
////            .enabled(true)
////            .build();
////
////        passwordResetToken = PasswordResetToken.builder()
////                .id(1L)
////                .userId(1L)
////                .token("token")
////                .expiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
////                .build();
////    }
//
//    //    @Test
//    //    void getMyProfile() {
//    //        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));
//    //        var response = userService.getMyProfile(storedUser.getId());
//    //        assertEquals(storedUser.getId(), response.getId());
//    //        verify(userService).getMyProfile(storedUser.getId());
//    //    }
//
//    //    @Test
//    //    void getMyProfileWhenUserNotFound() {
//    //        when(userRepository.findById(1L)).thenReturn(null);
//    //        assertThrows(UserNotFoundException.class, () -> userService.getMyProfile(storedUser.getId()));
//    //    }
//
//    //    @Test
//    //    void isExistingToken() {
//    //        when(passwordResetTokenRepository.findByToken(passwordResetToken.getToken())).thenReturn(Optional.of(passwordResetToken));
//    //        var response = userService.isExistingToken(passwordResetToken.getToken());
//    //        assertEquals(true, response);
//    //
//    //        assertEquals();
//    //    }
//}
