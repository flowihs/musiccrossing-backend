package ru.github.musiccrossing.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.dto.request.AlbumUpdateRequest;
import ru.github.musiccrossing.music.dto.response.AlbumCreateResponse;
import ru.github.musiccrossing.music.dto.response.AlbumFullDto;
import ru.github.musiccrossing.music.dto.response.AlbumSummaryDto;
import ru.github.musiccrossing.music.entity.Album;
import ru.github.musiccrossing.music.exception.AccessDeniedException;
import ru.github.musiccrossing.music.exception.AlbumNotFoundException;
import ru.github.musiccrossing.music.mapper.AlbumMapper;
import ru.github.musiccrossing.music.repository.AlbumRepository;
import ru.github.musiccrossing.storage.service.S3StorageService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper mapper;
    private final AlbumRepository repository;
    private final S3StorageService s3StorageService;

    @Transactional
    public AlbumCreateResponse create(final CreateAlbumRequest request, final User user) {
        final Album albumEntity = mapper.toEntity(request, user);
        return mapper.toResponse(repository.save(albumEntity));
    }

    @Transactional(readOnly = true)
    public AlbumFullDto getById(final UUID id, final UUID currentUserId) {
        final Album album = repository.findById(id).orElseThrow(() -> new AlbumNotFoundException("Album not found with id: " + id));

        if (!album.getLoadFromUser().getId().equals(currentUserId) && !album.isPublicContent()) {
            throw new AccessDeniedException("User does not have access to this album");
        }

        return AlbumFullDto.fromEntity(album, s3StorageService);
    }

    @Transactional(readOnly = true)
    public Page<AlbumSummaryDto> getByUser(final UUID userId, final Pageable pageable) {
        final Page<Album> albums = repository.findByLoadFromUserId(userId, pageable);
        return albums.map(AlbumSummaryDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AlbumSummaryDto> getPublic(final Pageable pageable) {
        final Page<Album> albums = repository.findByPublicContentTrue(pageable);
        return albums.map(AlbumSummaryDto::fromEntity);
    }

    @Transactional
    public AlbumFullDto update(final UUID id, final AlbumUpdateRequest request, final UUID currentUserId) {
        final Album album = repository.findById(id).orElseThrow(() -> new AlbumNotFoundException("Album not found with id: " + id));

        if (!album.getLoadFromUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User does not have access to this album");
        }

        album.setArtistName(request.artistName());
        album.setAlbumName(request.albumName());

        return AlbumFullDto.fromEntity(repository.save(album), s3StorageService);
    }

    @Transactional
    public void deleteById(final UUID id, final UUID currentUserId) {
        final Album album = repository.findById(id).orElseThrow(() -> new AlbumNotFoundException("Album not found with id: " + id));

        if (!album.getLoadFromUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User does not have access to this album");
        }

        repository.deleteById(id);
    }
}
