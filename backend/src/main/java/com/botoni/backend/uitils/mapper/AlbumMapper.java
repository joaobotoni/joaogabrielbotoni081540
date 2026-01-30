package com.botoni.backend.uitils.mapper;

import com.botoni.backend.dtos.album.AlbumRequest;
import com.botoni.backend.dtos.album.AlbumResponse;
import com.botoni.backend.entities.Album;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    default AlbumResponse map(Album album) {
        if (album == null) return null;

        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getYear()
        );
    }

    @Mapping(target = "id", ignore = true)
    Album map(AlbumRequest request);

    @Mapping(target = "id", ignore = true)
    void map(@MappingTarget Album album, AlbumRequest request);
}