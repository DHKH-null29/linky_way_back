package com.wnis.linkyway.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final Key key;
    private static final String CLAIM_NAME = "cName";

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public <T extends Authentication> String createAccessToken(T authentication) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(jwtPrincipal.getId().toString())
                .claim(CLAIM_NAME, jwtPrincipal.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() +
                                jwtProperties.getAccessToken().getValidTime() * 1000 * 60)
                )
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        JwtPrincipal jwtPrincipal = new JwtPrincipal(
                Long.parseLong(claims.getSubject()),
                claims.get(CLAIM_NAME).toString());
        return new JwtAuthenticationToken(jwtPrincipal, "", null);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException |
                IllegalArgumentException | UnsupportedJwtException e) {
            log.info("잘못된 토큰입니다: '{}'", token);
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다: '{}'", token);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

}

