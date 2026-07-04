package ru.github.musiccrossing.music.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.entity.UserRole;
import ru.github.musiccrossing.auth.repository.UserRepository;
import ru.github.musiccrossing.auth.security.UserDetailsImpl;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.entity.Album;
import ru.github.musiccrossing.music.repository.AlbumRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlbumControllerTest {
    private SecurityContext securityContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository repository;

    @MockitoBean
    private S3Template s3Template;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@test.com")
                .username("test")
                .password("123")
                .enabled(true)
                .role(UserRole.USER)
                .build();
        user = userRepository.save(user);
        
        final UserDetailsImpl userDetails = new UserDetailsImpl(user);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void create() {
        final CreateAlbumRequest request = new CreateAlbumRequest("Artist Name", "Album Name");

        mockMvc.perform(post("/album")
                        .with(securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.albumName").value("Album Name"))
                .andExpect(jsonPath("$.artistName").value("Artist Name"));

        final Album savedAlbum = repository.findAll().getFirst();
        assertThat(savedAlbum).satisfies(album -> {
            assertThat(album.getId()).isNotNull();
            assertThat(album.getAlbumName()).isEqualTo("Album Name");
            assertThat(album.getArtistName()).isEqualTo("Artist Name");
            assertThat(album.isPublicContent()).isFalse();
            assertThat(album.getLoadFromUser().getUsername()).isEqualTo("test");
        });
    }

    @Test
    @SneakyThrows
    void createWithEmptyAlbumName() {
        final CreateAlbumRequest request = new CreateAlbumRequest("Artist Name", "");

        mockMvc.perform(post("/album")
                        .with(securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createWithEmptyArtistName() {
        final CreateAlbumRequest request = new CreateAlbumRequest("", "Album Name");

        mockMvc.perform(post("/album")
                        .with(securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
