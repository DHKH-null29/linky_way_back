package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.email.EmailCodeRequest;
import com.wnis.linkyway.dto.email.EmailConfirmRequest;
import com.wnis.linkyway.service.email.EmailService;
import com.wnis.linkyway.util.cookie.CookieUtil;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final CookieUtil cookieUtil;

    private static final int VERIFICATION_COOKIE_EXPIRATION_MINUTE = 10;
    private static final String VERIFICATION_COOKIE_NAME = "evc";

    @PostMapping("/code")
    @ApiOperation(value = "이메일 인증 코드 전송")
    public ResponseEntity<Response<Object>> sendEmailVerificationCode(
            @Validated(ValidationSequence.class) @RequestBody EmailCodeRequest codeRequest) {
        emailService.sendVerificationCode(codeRequest.getEmail());
        return ResponseEntity.ok(Response.builder()
                .code(HttpStatus.OK.value())
                .message("이메일로 인증코드를 전송했습니다.")
                .build());
    }

    @PostMapping("/confirm")
    @ApiOperation(value = "이메일 인증 코드 검증")
    public ResponseEntity<Response<Object>> verifyEmailCode(
            HttpServletResponse response,
            @Validated(ValidationSequence.class) @RequestBody EmailConfirmRequest codeRequest) {
        emailService.confirmVerificationCode(codeRequest.getEmail(), codeRequest.getCode());
        Cookie cookie = cookieUtil.createCookie(
                VERIFICATION_COOKIE_NAME,
                codeRequest.getEmail(),
                VERIFICATION_COOKIE_EXPIRATION_MINUTE);
        response.addCookie(cookie);
        return ResponseEntity.ok(Response.builder()
                .code(HttpStatus.OK.value())
                .message("이메일 인증 성공")
                .build());
    }

}
