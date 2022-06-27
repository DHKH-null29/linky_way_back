package com.wnis.linkyway.util.cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {CookieUtil.class})
class CookieUtilTest {

    MockHttpServletRequest request;

    @Autowired
    private CookieUtil cookieUtil;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        request.setCookies(new Cookie("name", "HI"));
    }

    @Test
    @DisplayName("존재하는 쿠키에 대해 조회할 경우 해당 쿠키를 반환한다.")
    void FindExistCookieByNameShouldReturnCookie() {
        Cookie resultCookie = cookieUtil.getCookieByCookieName(request, "name");
        assertThat(resultCookie).isNotNull();
        assertThat(resultCookie.getValue()).isEqualTo("HI");
    }

    @Test
    @DisplayName("존재하지 않는 쿠키에 대한 조회는 Null을 반환한다.")
    void FindNotExistCookieByNameNoShouldReturnNull() {
        Cookie resultCookie = cookieUtil.getCookieByCookieName(request, "gaeaegeag");
        assertThat(resultCookie).isNull();
    }

    @Test
    @DisplayName("쿠키 추가 테스트")
    void shouldAddCookie() {
        Cookie cookie = cookieUtil.createCookie("new", "newValue", 1);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("new");
        assertThat(cookie.getValue()).isEqualTo("newValue");
    }

}
