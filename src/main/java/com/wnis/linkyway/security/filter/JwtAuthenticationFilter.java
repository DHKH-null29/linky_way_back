package com.wnis.linkyway.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.dto.member.LoginResponse;
import com.wnis.linkyway.exception.common.BusinessException;
import com.wnis.linkyway.exception.common.InvalidValueException;
import com.wnis.linkyway.exception.error.ErrorCode;
import com.wnis.linkyway.exception.error.ErrorResponse;
import com.wnis.linkyway.security.jwt.JwtAuthenticationToken;
import com.wnis.linkyway.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    private static final String LOGIN_URL = "/api/members/login";

    public JwtAuthenticationFilter(ObjectMapper objectMapper, JwtProvider jwtProvider) {
        super(new AntPathRequestMatcher(LOGIN_URL));
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

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

}
