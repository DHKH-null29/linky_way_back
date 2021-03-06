package com.wnis.linkyway.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.tag.TagControllerIntegrationTest;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.PasswordRequest;
import com.wnis.linkyway.dto.member.UpdateMemberRequest;
import com.wnis.linkyway.security.testutils.WithMockMember;
import com.wnis.linkyway.util.cookie.CookieConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/member-test.sql")
class MemberControllerIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(TagControllerIntegrationTest.class);
    @Autowired
    WebApplicationContext ctx;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // MockMvc ??????
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // ?????? ??????
                .alwaysDo(print())
                .build();
    }

    @Nested
    @DisplayName("????????????")
    class JoinTest {

        @Test
        @DisplayName("?????? ?????????")
        void responseTest() throws Exception {
            Cookie cookie = new Cookie(CookieConstants.VERIFICATION_COOKIE_NAME, "marraas1101@naver.com");
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("marraas1101@naver.com")
                    .password("aAa1212!32")
                    .nickname("hello")
                    .build();
            mockMvc.perform(post("/api/members")
                    .cookie(cookie)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(joinRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
        }

        @Test
        @DisplayName("????????? ????????? ?????? ?????????")
        void shouldThrowValidationExceptionResponseTest() throws Exception {
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("marraas1101@navaerom")
                    .password("aAa1212!32")
                    .nickname("hello")
                    .build();
            mockMvc.perform(post("/api/members").contentType("application/json")
                    .content(objectMapper.writeValueAsString(joinRequest)))
                    .andExpect(status().is(400))
                    .andReturn();
        }

        @Test
        @DisplayName("?????? ?????? ????????? ?????? ?????????")
        void shouldThrowDuplicateExceptionResponseTest() throws Exception {
            Cookie cookie = new Cookie(CookieConstants.VERIFICATION_COOKIE_NAME, "marrin1101@naver.com");
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("marrin1101@naver.com")
                    .password("aAa1212!32")
                    .nickname("hello")
                    .build();
            mockMvc.perform(post("/api/members").contentType("application/json")
                    .cookie(cookie)
                    .content(objectMapper.writeValueAsString(joinRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
        }

    }

    @Nested
    @DisplayName("????????? ??????")
    class EmailTest {

        @Test
        @DisplayName("?????? ?????????")
        void responseTest() throws Exception {
            mockMvc.perform(get("/api/members/email?email=marrin1101@naver.com"))
                    .andExpect(status().is(200))
                    .andReturn();

        }

        @Test
        @DisplayName("???????????? ?????? ?????? ?????? ?????????")
        void shouldThrowNotFoundEmailExceptionResponseTest() throws Exception {
            mockMvc.perform(get("/api/members/email?email=marrin1101@naver1.com"))
                    .andExpect(status().is(404))
                    .andReturn();

        }

    }

    @Nested
    @DisplayName("??????????????? ?????? ?????????")
    class SearchMyPageTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {
            mockMvc.perform(get("/api/members/page/me"))
                    .andExpect(status().is(200))
                    .andReturn();

        }

    }

    @Nested
    @DisplayName("???????????? ?????? ?????????")
    class SetPasswordTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                    .password("asa!asd12324A")
                    .build();

            mockMvc.perform(put("/api/members/password").contentType("application/json")
                    .content(objectMapper.writeValueAsString(passwordRequest)))
                    .andExpect(status().is(200))
                    .andReturn();

        }

        @Test
        @DisplayName("????????? ????????? ?????? ?????????")
        void shouldThrowValidationExceptionResponseTest() throws Exception {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                    .password("aaaaa")
                    .build();

            mockMvc.perform(put("/api/members/password").contentType("application/json")
                    .content(objectMapper.writeValueAsString(passwordRequest)))
                    .andExpect(status().is(400))
                    .andReturn();
        }

    }

    @Nested
    @DisplayName("?????? ?????? ?????????")
    @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
    class UpdateMemberTest {
        
        @Test
        @DisplayName("?????? ?????????")
        void responseTest() throws Exception {
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                                                                         .nickname("hello").build();
            
            mockMvc.perform(put("/api/members/page/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateMemberRequest)))
                   .andExpect(status().is(200));
        }
    }
    
    @Nested
    @DisplayName("?????? ?????? ?????????")
    @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
    class DeleteMemberTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {
            mockMvc.perform(delete("/api/members"))
                    .andExpect(status().is(200))
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????? ?????? ?????????")
    class searchNicknameDuplicationInfoTest {

        @Test
        @DisplayName("?????? ?????????")
        void responseTest() throws Exception {
            mockMvc.perform(get("/api/members/nickname").param("nickname", "Zeratu1"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

}