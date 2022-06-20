package com.wnis.linkyway.util.cookie;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class CookieUtil {

    public Cookie getCookieByCookieName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst().orElse(null);
    }

    public Cookie createCookie(String cookieName, String value, int second) {
        Cookie token = new Cookie(cookieName, value);
        token.setHttpOnly(true);
        token.setMaxAge(second);
        token.setPath("/");
        return token;
    }
}
