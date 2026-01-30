package com.botoni.backend.uitils.mapper;

import com.botoni.backend.dtos.authentication.AuthenticationResponse;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(source = "username", target = "alias")
    User map(RegisterRequest request);

    default AuthenticationResponse map(User user, String token) {
        if (user == null) return null;

        return new AuthenticationResponse(
                user.getAlias(),
                user.getEmail(),
                token
        );
    }
}