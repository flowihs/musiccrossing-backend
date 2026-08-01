package ru.github.musiccrossing.music.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistUpdateDataRequest {
    private UUID id;
    private String name;
    private MultipartFile avatar;
}
