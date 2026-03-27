package com.toy.project.studio.config.jwt;

import java.util.List;

public record JwtPrincipal(
        Long userId,
        String username,
        List<String> roles
) {

    public JwtPrincipal {
        roles = List.copyOf(roles);
    }
}
