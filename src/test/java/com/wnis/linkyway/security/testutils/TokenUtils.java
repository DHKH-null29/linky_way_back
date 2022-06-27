package com.wnis.linkyway.security.testutils;

import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtPrincipal;
import com.wnis.linkyway.security.jwt.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenUtils {

    public static String accessToken(JwtProvider jwtProvider, Member member) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(
                new JwtPrincipal(member.getId(), member.getEmail()), "", null);

        setAuthentication(jwtAuthenticationToken);
        return jwtProvider.createAccessToken(jwtAuthenticationToken);
    }

    private static void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
