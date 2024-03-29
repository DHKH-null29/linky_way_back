package com.wnis.linkyway.security.filter;

import com.fasterxml.jackson.databind.*;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.dto.member.LoginResponse;
import com.wnis.linkyway.exception.common.InvalidValueException;
import com.wnis.linkyway.redis.RedisProvider;
import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class JwtAuthenticationFilterTest {

    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisProvider redisProvider = mock(RedisProvider.class);

    @InjectMocks
    private final JwtAuthenticationFilter jwtAuthenticationFilter
            = new JwtAuthenticationFilter(objectMapper, jwtProvider, redisProvider);

    private MockHttpServletRequest httpServletRequest;
    private MockHttpServletResponse httpServletResponse;

    private final LoginRequest loginRequest = new LoginRequest("ehd0309@naver.com", "Abc1234!!!");
    private final Authentication authentication = new JwtAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword(), null);

    static Stream<String> wrongJsons() {
        return Stream.of("", " ", "{}");
    }

    static Stream<LoginRequest> wrongLogins() {
        return Stream.of(
                new LoginRequest("", "AbcdeVx123!!"),
                new LoginRequest("ehd0309@naver.com", "   ")
        );
    }

    @BeforeEach
    void setup() {
        String LOGIN_URL = "/members/login";
        httpServletRequest = new MockHttpServletRequest("POST", LOGIN_URL);
        httpServletRequest.addHeader("Accept", "application/json");
        httpServletRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);

        httpServletResponse = new MockHttpServletResponse();
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("올바른 로그인 요청에 대한 인증 요청 수행 테스트")
    void successAttemptAuthenticationTest() throws Exception {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        httpServletRequest.setContent(objectMapper.writeValueAsBytes(loginRequest));
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse);

        verify(authenticationManager, times(1)).authenticate(authentication);
    }

    @Test
    @DisplayName("적절한 로그인 요청이나 이미 N회 이상 시도하여 로그인이 제한되어있을 경우의 테스트")
    void failAttemptAuthenticationWithRestriction() throws Exception {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        httpServletRequest.setContent(objectMapper.writeValueAsBytes(loginRequest));
        given(redisProvider.getData(any())).willReturn("999");
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);

        assertThatThrownBy(() -> jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse))
                .isInstanceOf(LockedException.class);
    }

    @ParameterizedTest
    @DisplayName("올바르지 않은 Json 포맷의 요청에 대한 예외 반환 테스트")
    @MethodSource("wrongJsons")
    void failAttemptAuthenticationWithInvalidJsonTest(String wrongJson) throws Exception {
        String errorMsg = "올바르지 않은 포맷의 요청";

        httpServletRequest.setContent(objectMapper.writeValueAsBytes(wrongJson));

        assertThatThrownBy(() -> jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse))
                .isInstanceOfSatisfying(UsernameNotFoundException.class, e -> {
                    assertThat(e.getMessage()).isEqualTo(errorMsg);
                })
                .hasCause(new InvalidValueException(""));
    }

    @ParameterizedTest
    @DisplayName("올바르지 않은 로그인 데이터의 요청에 대한 예외 반환 테스트")
    @MethodSource("wrongLogins")
    void failAttemptAuthenticationWithInvalidFormatTest(LoginRequest loginRequest) throws Exception {
        String errorMsg = "이메일 또는 비밀번호 입력을 확인하세요";

        httpServletRequest.setContent(objectMapper.writeValueAsBytes(loginRequest));

        assertThatThrownBy(() -> jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse))
                .isInstanceOfSatisfying(UsernameNotFoundException.class, e -> {
                    assertThat(e.getMessage()).isEqualTo(errorMsg);
                })
                .hasCause(new InvalidValueException(""));
    }

    @Test
    @DisplayName("인증 성공 핸들러 동작 테스트")
    void successfulAuthenticationTest() throws Exception {
        String tokenName = "access_token";
        httpServletRequest.setContent(objectMapper.writeValueAsBytes(loginRequest));
        given(jwtProvider.createAccessToken(authentication)).willReturn(tokenName);

        jwtAuthenticationFilter.successfulAuthentication(httpServletRequest, httpServletResponse, null, authentication);

        Response<LoginResponse> response =
                objectMapper.readValue(httpServletResponse.getContentAsString(), Response.class);
        LoginResponse loginResponse = objectMapper.convertValue(response.getData(), LoginResponse.class);

        assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(loginResponse.getAccessToken()).isEqualTo(tokenName);
    }

    @Test
    @DisplayName("인증 실패 핸들러 동작 테스트")
    void unSuccessFulAuthenticationTest() throws Exception {
        jwtAuthenticationFilter.unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new LockedException("hi"));
        verify(redisProvider)
                .setDataWithExpiration(anyString(), anyString(), anyLong());
    }

}
