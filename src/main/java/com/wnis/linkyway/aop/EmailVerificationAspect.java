package com.wnis.linkyway.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.exception.common.EmailSendException;
import com.wnis.linkyway.util.cookie.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.wnis.linkyway.util.cookie.CookieConstants.*;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EmailVerificationAspect {

    private final CookieUtil cookieUtil;
    private final HttpServletRequest httpServletRequest;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.wnis.linkyway.aop.WithEmailVerification)")
    public void withEmailVerification() {
    }

    @Around(
            "withEmailVerification() && " +
                    "execution(* *())"
    )
    public Object simpleProcessWithoutParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    @Around(
            "withEmailVerification() && " +
                    "execution(* *(@org.springframework.web.bind.annotation.RequestBody (*), ..)) && " +
                    "args(body, ..)"
    )
    public Object processWithBody(ProceedingJoinPoint joinPoint, Object body) throws Throwable {
        Cookie cookie = cookieUtil.getCookieByCookieName(httpServletRequest, VERIFICATION_COOKIE_NAME);
        checkCookieExists(cookie);
        checkRequestEmailEqualsWithCookie(cookie, body);
        return joinPoint.proceed();
    }

    private void checkRequestEmailEqualsWithCookie(Cookie cookie, Object body) {
        try {
            String email = getValueOfKeyFromBodyRequest("email", body);
            if (!cookie.getValue().equals(email)) {
                throw new EmailSendException("이메일 정보가 올바르지 않습니다");
            }
        } catch (JsonProcessingException | JSONException e) {
            log.error("", e);
            throw new EmailSendException("요청 데이터로부터 이메일 정보를 얻을 수 없음");
        }
    }

    private String getValueOfKeyFromBodyRequest(String key, Object body) throws JsonProcessingException, JSONException {
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(body));
        return jsonObject.getString(key);
    }

    private void checkCookieExists(Cookie cookie) {
        if (cookie == null) {
            throw new EmailSendException("이메일 인증이 필요한 요청입니다.");
        }
    }

}
