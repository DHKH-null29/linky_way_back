package com.wnis.linkyway.security.testutils;

import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockMemberSecurityContextFactory
        implements WithSecurityContextFactory<WithMockMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockMember mockMember) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        JwtPrincipal jwtPrincipal =
                new JwtPrincipal(mockMember.id(), mockMember.email());

        JwtAuthenticationToken jwtAuthenticationToken =
                new JwtAuthenticationToken(jwtPrincipal, "password",
                        null);

        context.setAuthentication(jwtAuthenticationToken);
        return context;
    }

}
