package com.wnis.linkyway.security.jwt;

import lombok.Getter;

@Getter
public class JwtPrincipal {

    private final Long id;
    private final String email;

    public JwtPrincipal(Long id, String email) {
        this.id = id;
        this.email = email;
    }

}
