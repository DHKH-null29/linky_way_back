package com.wnis.linkyway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.tag.TagControllerIntegrationTest;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.PasswordRequest;
import com.wnis.linkyway.security.testutils.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

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
        // MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .apply(springSecurity())
                                 .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필터 추가
                                 .alwaysDo(print())
                                 .build();
    }

    @Nested
    @DisplayName("회원가입")
    class JoinTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws Exception {
            JoinRequest joinRequest = JoinRequest.builder()
                                                 .email("marraas1101@naver.com")
                                                 .password("aAa1212!32")
                                                 .nickname("hello")
                                                 .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/members").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsString(joinRequest)))
                                         .andExpect(status().is(200))
                                         .andReturn();
        }

        @Test
        @DisplayName("유효성 핸들러 응답 테스트")
        void shouldThrowValidationExceptionResponseTest() throws Exception {
            JoinRequest joinRequest = JoinRequest.builder()
                                                 .email("marraas1101@navaerom")
                                                 .password("aAa1212!32")
                                                 .nickname("hello")
                                                 .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/members").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsString(joinRequest)))
                                         .andExpect(status().is(400))
                                         .andReturn();
        }

        @Test
        @DisplayName("중복 예외 핸들러 응답 테스트")
        void shouldThrowDuplicateExceptionResponseTest() throws Exception {
            JoinRequest joinRequest = JoinRequest.builder()
                                                 .email("marrin1101@naver.com")
                                                 .password("aAa1212!32")
                                                 .nickname("hello")
                                                 .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/members").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsString(joinRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();
        }

    }

    @Nested
    @DisplayName("이메일 조회")
    class EmailTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws Exception {

            MvcResult mvcResult = mockMvc.perform(get("/api/members/email?email=marrin1101@naver.com"))
                                         .andExpect(status().is(200))
                                         .andReturn();

        }

        @Test
        @DisplayName("이메일이 없는 경우 조회 테스트")
        void shouldThrowNotFoundEmailExceptionResponseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/api/members/email?email=marrin1101@naver1.com"))
                                         .andExpect(status().is(404))
                                         .andReturn();

        }

    }

    @Nested
    @DisplayName("마이페이지 조회 테스트")
    class SearchMyPageTest {

        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {

            MvcResult mvcResult = mockMvc.perform(get("/api/members/page/me"))
                                         .andExpect(status().is(200))
                                         .andReturn();

        }

    }

    @Nested
    @DisplayName("패스워드 변경 테스트")
    class SetPasswordTest {

        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                                                             .password("asa!asd12324A")
                                                             .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/members/password").contentType("application/json")
                                                                              .content(objectMapper.writeValueAsString(passwordRequest)))
                                         .andExpect(status().is(200))
                                         .andReturn();

        }

        @Test
        @DisplayName("유효성 핸들러 응답 테스트")
        void shouldThrowValidationExceptionResponseTest() throws Exception {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                                                             .password("aaaaa")
                                                             .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/members/password").contentType("application/json")
                                                                              .content(objectMapper.writeValueAsString(passwordRequest)))
                                         .andExpect(status().is(400))
                                         .andReturn();
        }

    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
    class DeleteMemberTest {

        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@hanmail.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/members"))
                                         .andExpect(status().is(200))
                                         .andReturn();
        }
    }

    @Nested
    @DisplayName("닉네임 중복 여부 조회 테스트")
    class searchNicknameDuplicationInfoTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws Exception {
            mockMvc.perform(get("/api/members/nickname").param("nickname", "Zeratu1"))
                   .andExpect(status().isOk())
                   .andDo(print());
        }
    }

}