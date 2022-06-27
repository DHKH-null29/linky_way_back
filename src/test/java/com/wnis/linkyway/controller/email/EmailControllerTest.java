package com.wnis.linkyway.controller.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.EmailController;
import com.wnis.linkyway.dto.email.EmailCodeRequest;
import com.wnis.linkyway.dto.email.EmailConfirmRequest;
import com.wnis.linkyway.service.email.EmailService;
import com.wnis.linkyway.util.cookie.CookieUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EmailController.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class)})
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private CookieUtil cookieUtil;

    private final String API = "/api/email";
    private final String EMAIL = "ehd0309@naver.com";

    @Test
    @DisplayName("이메일 인증 코드 전송 응답 테스트")
    void sendEmailVerificationCodeTest() throws Exception {
        EmailCodeRequest emailCodeRequest = new EmailCodeRequest(EMAIL);

        mockMvc.perform(post(API + "/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailCodeRequest)))
                .andExpect(status().isOk())
                .andDo(print());
        verify(emailService).sendVerificationCode(EMAIL);
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 응답 테스트")
    void verifyEmailCodeTest() throws Exception {
        EmailConfirmRequest emailConfirmRequest = new EmailConfirmRequest(EMAIL, "CODE");
        Cookie cookie = new Cookie("key", EMAIL);
        given(cookieUtil.createCookie(anyString(), anyString(), anyInt()))
                .willReturn(cookie);
        MvcResult mvcResult = mockMvc.perform(post(API + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Assertions.assertThat(mvcResult.getResponse().getHeader("Set-Cookie"))
                .isNotBlank();
    }

}
