package com.wnis.linkyway.security.jwt;

import com.wnis.linkyway.config.PropertiesConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {PropertiesConfiguration.class, JwtProvider.class})
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiY05hbWUiOiJlaGQwMzA5QG5hdmVyLmNvbSIsImlhdCI6MTY1Mjg1OTgxMCwiZXhwIjoxNjUyODU5ODEwfQ.GkZU7vAGCBhOjf3lP-EmF9C9Y6A2gcjLcvOWjUKzl_d7bMI-z4hyPznYlTVFOKuO8cSqyUBfjv2nJ-cVY_OLJQ";
    private static final String WRONG_TOKEN = "eyJhbGciOiJIUzUxMiJ9.abczdWIiOiIxIiwiY05hbWUiOiJlaGQwMzA5QG5hdmVyLmNvbSIsImlhdCI6MTY1Mjg1OTgxMCwiZXhwIjoxNjUyODU5ODEwfQ.GkZU7vAGCBhOjf3lP-EmF9C9Y6A2gcjLcvOWjUKzl_d7bMI-z4hyPznYlTVFOKuO8cSqyUBfjv2nJ-cVY_OLJQ";

    private static final Long id = 1L;
    private static final String email = "ehd0309@naver.com";
    private final SampleAuthenticationImpl authentication =
            new SampleAuthenticationImpl(new JwtPrincipal(id, email));

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void createAccessTokenTest() {
        assertThat(jwtProvider.createAccessToken(authentication))
                .isNotNull();
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("토큰 유효성 검사 테스트")
    class ValidTokenTest {
        Stream<String> wrongTokens() {
            return Stream.of("", " ", null, EXPIRED_TOKEN, WRONG_TOKEN);
        }

        @ParameterizedTest
        @MethodSource("wrongTokens")
        @DisplayName("잘못된 토큰 입력에 대해 false를 리턴한다.")
        void shouldReturnFalseWithWrongTokens(String token) {
            assertThat(jwtProvider.validateToken(token))
                    .isFalse();
        }

        @Test
        @DisplayName("정상적인 토큰에 대해 true를 리턴한다.")
        void shouldReturnTrueWithNormalToken() {
            String normalAccessToken = jwtProvider.createAccessToken(authentication);
            assertThat(jwtProvider.validateToken(normalAccessToken))
                    .isTrue();
        }

    }

    @Test
    @DisplayName("정상적인 토큰에서 인증정보를 얻는 동작 테스트")
    void getAuthenticationTest() {
        String normalAccessToken = jwtProvider.createAccessToken(authentication);
        assertThat(jwtProvider.getAuthentication(normalAccessToken))
                .isNotNull()
                .isInstanceOfSatisfying(JwtAuthenticationToken.class, jwtAuthenticationToken -> {
                    assertThat(jwtAuthenticationToken).isNotNull();
                    assertThat(jwtAuthenticationToken.getPrincipal())
                            .isNotNull()
                            .isInstanceOfSatisfying(JwtPrincipal.class, jwtPrincipal -> {
                                assertThat(jwtPrincipal)
                                        .usingRecursiveComparison()
                                        .isEqualTo(authentication.getPrincipal());
                            });
                });
    }

    static class SampleAuthenticationImpl extends AbstractAuthenticationToken {

        private final Object principal;

        public SampleAuthenticationImpl(Object principal) {
            super(null);
            this.principal = principal;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return this.principal;
        }

    }

}
