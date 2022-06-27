package com.wnis.linkyway.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secretKey;
    private final AccessToken accessToken;

    @Getter
    @RequiredArgsConstructor
    public static final class AccessToken {
        private final String name;
        private final Long validTime;
    }

}
