package com.wnis.linkyway.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import com.wnis.linkyway.security.testutils.WithMockMember;
import com.wnis.linkyway.service.tag.TagService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/initialize-test.sql")
public class TagControllerIntegrationTest {
    
    private final Logger logger = LoggerFactory.getLogger(TagControllerIntegrationTest.class);
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    
    @BeforeEach
    void setup() {
        
        //MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
        
        Member member = Member.builder()
                .email("marrin1101@naver.com")
                .nickname("Hyeon")
                .password("A1!a1234")
                .build();
        
        Tag tag1 = Tag.builder()
                .name("Spring")
                .shareable(true)
                .views(0)
                .build();
        
        Tag tag2 = Tag.builder()
                .name("food1")
                .shareable(false)
                .views(10)
                .build();
        
        member.addTag(tag1).addTag(tag2);
        tagRepository.saveAll(Arrays.asList(tag1, tag2));
        memberRepository.save(member);
    }
    
    @Nested
    @DisplayName("태그 조회")
    class SearchTagsTest {
        
        @Test
        @DisplayName("태그 조회 Response")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            Response response = tagService.searchTags(1L);
            ResponseEntity<Response> expected = ResponseEntity.ok(response);
            
            MvcResult mvcResult = mockMvc.perform(get("/api/tags/table"))
                    .andExpect(status().is(200))
                    .andReturn();
            
            String s = mvcResult.getResponse().getContentAsString();
            logger.info(s);
            
        }
    }
    
    @Nested
    @DisplayName("태그 추가")
    class AddTagTest {
        
        @Test
        @DisplayName("태그 추가 Response")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void ResponseTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("hello")
                    .shareable("true")
                    .build();
            MvcResult mvcResult = mockMvc.perform(post("/api/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagRequest)))
                    .andExpect(status().isOk())
                    .andReturn();
            String res = mvcResult.getResponse().getContentAsString();
            logger.info(res);
            
        }
        
        @Test
        @DisplayName("중복시 핸들러 Response")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void ResponseExceptionTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("Spring")
                    .shareable("true")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
    }
    
    @Nested
    @DisplayName("태그 수정")
    
    class SetTag {
        
        @Test
        @DisplayName("수정 Response")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("Summer")
                    .shareable("false")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/tags/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @Test
        @DisplayName("없는데 수정 예외 핸들러")
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseExceptionTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("Summer")
                    .shareable("false")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/tags/4")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
    }
    
    @Nested
    @DisplayName("태그 삭제")
    class DeleteTag {
        
        @DisplayName("삭제 Response")
        @Test
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/tags/1")
                    )
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @DisplayName("없는데 삭제 예외 핸들러")
        @Test
        @WithMockMember(id = 1, email = "marrin1101@naver.com")
        void responseExceptionTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/tags/5")
                    )
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
    }
}
