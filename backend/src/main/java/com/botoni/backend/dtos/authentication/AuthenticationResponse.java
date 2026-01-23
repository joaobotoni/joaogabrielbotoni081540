package com.botoni.backend.dtos.authentication;


import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(
        String username,
        String email
) {

}
