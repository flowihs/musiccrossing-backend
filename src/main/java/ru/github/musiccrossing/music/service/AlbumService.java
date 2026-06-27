package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.dto.response.AlbumCreateResponse;
import ru.github.musiccrossing.music.entity.Album;
import ru.github.musiccrossing.music.mapper.AlbumMapper;
import ru.github.musiccrossing.music.repository.AlbumRepository;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper mapper;
    private final AlbumRepository repository;

    @Transactional
    public AlbumCreateResponse create(final CreateAlbumRequest request, final User user) {
        final Album albumEntity = mapper.toEntity(request, user);
        return mapper.toResponse(repository.save(albumEntity));
    }
}
