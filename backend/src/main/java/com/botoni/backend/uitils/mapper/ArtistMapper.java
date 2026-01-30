package com.botoni.backend.uitils.mapper;

import com.botoni.backend.dtos.artist.ArtistRequest;
import com.botoni.backend.dtos.artist.ArtistResponse;
import com.botoni.backend.entities.Artist;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArtistMapper {

    default ArtistResponse map(Artist artist) {
        if (artist == null) return null;
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getGenre()
        );
    }

    @Mapping(target = "genre", ignore = true)
    Artist map(ArtistRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genre", ignore = true)
    void map(@MappingTarget Artist artist, ArtistRequest request);
}