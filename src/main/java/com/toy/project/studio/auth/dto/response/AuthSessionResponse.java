package com.toy.project.studio.auth.dto.response;

public record AuthSessionResponse(
        boolean authenticated
) {
    public static AuthSessionResponse of(boolean authenticated) {
        return new AuthSessionResponse(authenticated);
    }
}
