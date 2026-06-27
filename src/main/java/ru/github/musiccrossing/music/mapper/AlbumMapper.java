package ru.github.musiccrossing.music.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.music.dto.request.CreateAlbumRequest;
import ru.github.musiccrossing.music.dto.response.AlbumCreateResponse;
import ru.github.musiccrossing.music.entity.Album;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(target = "loadFromUser", source = "user")
    @Mapping(target = "publicContent", constant = "false")
    Album toEntity(CreateAlbumRequest request, User user);

    AlbumCreateResponse toResponse(Album album);
}
