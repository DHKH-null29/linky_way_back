package com.wnis.linkyway.security.filter;

import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtPrincipal;
import com.wnis.linkyway.security.jwt.JwtProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtAuthorizationFilterTest {

    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    private final FilterChain filterChain = mock(FilterChain.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private MockHttpServletRequest httpServletRequest;
    private MockHttpServletResponse httpServletResponse;

    @InjectMocks
    private final JwtAuthorizationFilter jwtAuthorizationFilter
            = new JwtAuthorizationFilter(authenticationManager, jwtProvider);

    private final Authentication authentication = new JwtAuthenticationToken(
            new JwtPrincipal(1L, "ehd0309@naver.com"), "", null);

    @BeforeAll
    void setup() {
        httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("Accept", "application/json");
        httpServletRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletRequest.addHeader(AUTHORIZATION_HEADER, "jwt");

        httpServletResponse = new MockHttpServletResponse();
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("헤더에 정상적인 토큰이 존재해 인증 ")
    void doAuthorizationFilterInternalTest() throws Exception {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        given(jwtProvider.validateToken(accessToken)).willReturn(true);
        given(jwtProvider.getAuthentication(accessToken)).willReturn(authentication);

        jwtAuthorizationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        Authentication resultAuthentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(resultAuthentication)
                .isNotNull()
                .isInstanceOfSatisfying(JwtAuthenticationToken.class, jwtAuthenticationToken -> {
                    assertThat(jwtAuthenticationToken.getPrincipal())
                            .isNotNull()
                            .usingRecursiveComparison()
                            .isEqualTo(authentication.getPrincipal());
                });

        verify(filterChain,times(1)).doFilter(httpServletRequest,httpServletResponse);
    }

}
