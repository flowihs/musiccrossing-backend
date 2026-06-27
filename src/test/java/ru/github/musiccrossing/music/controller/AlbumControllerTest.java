package ru.github.musiccrossing.music.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlbumControllerTest {
    private static SecurityContext securityContext;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AlbumRepository repository;

    @MockitoBean
    private S3Template s3Template;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        final User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .username("test")
                .password("123")
                .enabled(true)
                .role(UserRole.USER)
                .build();
        final UserDetailsImpl userDetails = new UserDetailsImpl(user);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
    }

    @Test
    @SneakyThrows
    void create() {
        final UUID albumId = UUID.randomUUID();
        final CreateAlbumRequest request = new CreateAlbumRequest("Artist Name", "Album Name");
        final Album savedAlbum = new Album();
        savedAlbum.setId(albumId);
        savedAlbum.setAlbumName("Album Name");
        savedAlbum.setArtistName("Artist Name");

        when(repository.save(any(Album.class))).thenReturn(savedAlbum);

        mockMvc.perform(post("/album")
                        .with(securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumId.toString()))
                .andExpect(jsonPath("$.albumName").value("Album Name"))
                .andExpect(jsonPath("$.artistName").value("Artist Name"));

        final ArgumentCaptor<Album> captor = ArgumentCaptor.forClass(Album.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue()).satisfies(album -> {
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
