package com.wnis.linkyway.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.tag.TagRequest;
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
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/tag-test.sql")
public class TagControllerIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    @DisplayName("태그 조회")
    class SearchTagsTest {

        @Test
        @DisplayName("반드시 응답해야 할 프로퍼티 존재 검증")
        @WithMockMember
        void mustReturnResponseFormatExistTest() throws Exception {
            mockMvc.perform(get("/api/tags/table"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$..tagId").exists())
                   .andExpect(jsonPath("$..tagName").exists())
                   .andExpect(jsonPath("$..isPublic").exists());
        }
    }

    @Nested
    @DisplayName("태그 추가")
    class AddTagTest {

        @Test
        @DisplayName("반드시 응답해야 할 프로퍼티 존재 검증")
        @WithMockMember
        void mustReturnResponseFormatExistTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName("dance")
                                              .isPublic("false")
                                              .build();

            mockMvc.perform(post("/api/tags").contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(tagRequest)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isMap())
                   .andExpect(jsonPath("$..tagId").exists())
                   .andExpect(jsonPath("$..tagName").doesNotExist())
                   .andExpect(jsonPath("$..shareable").doesNotExist())
                   .andExpect(jsonPath("$..views").doesNotExist());
        }

        @Test
        @DisplayName("중복시 핸들러 Response")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void ResponseExceptionTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName("spring")
                                              .isPublic("true")
                                              .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/tags").contentType(MediaType.APPLICATION_JSON)
                                                                   .content(objectMapper.writeValueAsString(tagRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }
    }

    @Nested
    @DisplayName("태그 수정")
    class SetTag {

        @Test
        @DisplayName("반드시 응답해야 할 프로퍼티 존재 검증")
        @WithMockMember
        void mustReturnResponseFormatExistTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName("dance")
                                              .isPublic("false")
                                              .build();

            mockMvc.perform(put("/api/tags/1").contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(tagRequest)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isMap())
                   .andExpect(jsonPath("$..tagId").exists())
                   .andExpect(jsonPath("$..tagName").exists())
                   .andExpect(jsonPath("$..isPublic").exists())
                   .andExpect(jsonPath("$..views").doesNotExist());
        }

        @Test
        @DisplayName("없는데 수정 예외 핸들러")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseExceptionTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName("Summer")
                                              .isPublic("false")
                                              .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/tags/4000").contentType(MediaType.APPLICATION_JSON)
                                                                       .content(objectMapper.writeValueAsString(tagRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }
    }

    @Nested
    @DisplayName("태그 삭제")
    class DeleteTag {

        @Test
        @DisplayName("반드시 응답해야 할 프로퍼티 존재 검증")
        @WithMockMember
        void mustReturnResponseFormatExistTest() throws Exception {
            mockMvc.perform(delete("/api/tags/1"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isMap())
                   .andExpect(jsonPath("$..tagId").exists())
                   .andExpect(jsonPath("$..tagName").doesNotExist())
                   .andExpect(jsonPath("$..shareable").doesNotExist())
                   .andExpect(jsonPath("$..views").doesNotExist());
        }

        @DisplayName("없는데 삭제 예외 핸들러")
        @Test
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseExceptionTest() throws Exception {
            mockMvc.perform(delete("/api/tags/1000"))
                   .andExpect(status().is(404));
        }
    }
}
