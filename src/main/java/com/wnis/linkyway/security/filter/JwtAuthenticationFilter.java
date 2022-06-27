package com.wnis.linkyway.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.dto.member.LoginResponse;
import com.wnis.linkyway.exception.common.BusinessException;
import com.wnis.linkyway.exception.common.InvalidValueException;
import com.wnis.linkyway.exception.error.ErrorCode;
import com.wnis.linkyway.exception.error.ErrorResponse;
import com.wnis.linkyway.redis.RedisProvider;
import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;

import static com.wnis.linkyway.redis.RedisConstants.*;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;

    private static final String LOGIN_URL = "/api/members/login";
    // NGINX 기준 헤더네임으로 서버 변경이 예상될 경우 프로파일 활용해 동적으로 처리
    private static final String PROXY_HEADER = "X-Forwarded-For";

    public JwtAuthenticationFilter(ObjectMapper objectMapper, JwtProvider jwtProvider, RedisProvider redisProvider) {
        super(new AntPathRequestMatcher(LOGIN_URL));
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
        this.redisProvider = redisProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        checkLoginAttemptRestricted(getIpFromRequest(request));
        LoginRequest loginRequest = getObjectFromInputStream(request.getInputStream(), LoginRequest.class);
        validLoginRequest(loginRequest);

        return getAuthenticationManager().authenticate(toAuthentication(loginRequest));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authResult;
        String accessToken = jwtProvider.createAccessToken(jwtAuthenticationToken);

        Response<LoginResponse> loginResponseResponse =
                Response.of(HttpStatus.OK, new LoginResponse(accessToken), "로그인 성공");
        sendResponse(response.getStatus(), response, loginResponseResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException authException) throws IOException, ServletException {
        increaseLoginAttemptRestrictedCount(getIpFromRequest(request));
        ErrorCode errorCode = isBusinessException(authException) ? ErrorCode.INVALID_INPUT_VALUE : ErrorCode.UNAUTHORIZED;
        int status = errorCode.getCode();
        String message = authException.getMessage();
        sendResponse(status, response, ErrorResponse.of(errorCode, message, LOGIN_URL));
    }

    private Authentication toAuthentication(LoginRequest loginRequest) {
        return new JwtAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), null);
    }

    private void validLoginRequest(LoginRequest loginRequest) throws AuthenticationException {
        if (!StringUtils.hasText(loginRequest.getEmail()) || !StringUtils.hasText(loginRequest.getPassword())) {
            throw new UsernameNotFoundException("이메일 또는 비밀번호 입력을 확인하세요", new InvalidValueException(""));
        }
    }

    private <T> T getObjectFromInputStream(ServletInputStream inputStream, Class<T> tClass) {
        try {
            return objectMapper.readValue(inputStream, tClass);
        } catch (IOException e) {
            throw new UsernameNotFoundException("올바르지 않은 포맷의 요청", new InvalidValueException(""));
        }
    }

    private boolean isBusinessException(AuthenticationException authenticationException) {
        return authenticationException.getCause() instanceof BusinessException;
    }

    private void sendResponse(int status, HttpServletResponse response, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getWriter()
                .write(objectMapper.writeValueAsString(data));
    }

    private void increaseLoginAttemptRestrictedCount(String ip) {
        int currentCount = getLoginAttemptCount(ip);
        if (isPossibleRangeForIncreaseCount(currentCount)) {
            redisProvider.setDataWithExpiration(
                    LOGIN_KEY_PREFIX + ip, String.valueOf(currentCount + 1), LOGIN_RESTRICT_TIMEOUT_MILLS);
        }
    }

    private boolean isPossibleRangeForIncreaseCount(int currentCount) {
        return currentCount >= 0 && currentCount <= LOGIN_RESTRICT_COUNT;
    }

    private void checkLoginAttemptRestricted(String ip) {
        if (getLoginAttemptCount(ip) >= LOGIN_RESTRICT_COUNT) {
            throw new LockedException(
                    MessageFormat.format(
                            "{0}회 로그인 실패 시 {1}분간 로그인 하실 수 없습니다",
                            LOGIN_RESTRICT_COUNT,
                            LOGIN_RESTRICT_TIMEOUT_MILLS / 60000));
        }
    }

    private String getIpFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(PROXY_HEADER))
                .orElse(request.getRemoteAddr());
    }

    private int getLoginAttemptCount(String ip) {
        return Integer.parseInt(
                Optional.ofNullable(redisProvider.getData(LOGIN_KEY_PREFIX + ip))
                        .orElse("0"));
    }

}
