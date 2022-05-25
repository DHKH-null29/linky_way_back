package com.wnis.linkyway.security.provider;

import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtPrincipal;
import com.wnis.linkyway.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Optional<Authentication> optionalAuthentication =
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        return optionalAuthentication.orElseGet(() -> {
            Member member = getMemberFromAuthentication(authentication);
            return new JwtAuthenticationToken(toPrincipal(member), "", null);
        });
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private JwtPrincipal toPrincipal(Member member) {
        return new JwtPrincipal(member.getId(), member.getEmail());
    }

    private Member getMemberFromAuthentication(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        return memberService.login(
                LoginRequest.builder()
                        .email(email)
                        .password(password)
                        .build()
        );
    }

}
