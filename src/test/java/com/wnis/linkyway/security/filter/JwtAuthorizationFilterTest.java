package com.wnis.linkyway.security.filter;

import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtPrincipal;
import com.wnis.linkyway.security.jwt.JwtProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;

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

        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Nested
    @DisplayName("필터 동작 제외 메소드 테스트")
    class ShouldNotFilterTest {

        @ParameterizedTest
        @ValueSource(strings = {"/api", "/api/members"})
        @DisplayName("비즈니스 요청에 대해서 true를 반환한다.")
        void shouldReturnTrue(String path) throws Exception {
            httpServletRequest.setServletPath(path);
            assertThat(jwtAuthorizationFilter.shouldNotFilter(httpServletRequest)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"/swagger-ui", "/h2-console", "/"})
        @DisplayName("비즈니스 이외의 요청에 대해서 false를 반환한다.")
        void shouldReturnFalse(String path) throws Exception {
            httpServletRequest.setServletPath(path);
            assertThat(jwtAuthorizationFilter.shouldNotFilter(httpServletRequest)).isTrue();
        }

    }

}
