package com.wnis.linkyway.controller.card;

import com.wnis.linkyway.entity.*;
import com.wnis.linkyway.security.testutils.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/initialize-test.sql")
public class CardIntegrationTest {
    
    @Autowired MockMvc mockMvc;
    
    @Autowired WebApplicationContext ctx;
    
    @Autowired EntityManager entityManager;
    
    @BeforeEach
    void setup() {
        // MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .apply(springSecurity())
                                 .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필터 추가
                                 .alwaysDo(print())
                                 .build();
    
        Member member = Member.builder()
                              .email("marrin1101@naver.com")
                              .nickname("helloworld")
                              .password("asda!!!12123A1")
                              .build();
    
        Folder folder = Folder.builder()
                              .member(member)
                              .name("default")
                              .depth(0L)
                              .build();
    
        Folder folder2 = Folder.builder()
                               .member(member)
                               .name("f1")
                               .depth(1L)
                               .build();
    
        Card card = Card.builder()
                        .folder(folder2)
                        .link("www.google.co.kr")
                        .title("hello")
                        .content("yeah")
                        .isPublic(false)
                        .build();
    
        Card card2 = Card.builder()
                         .folder(folder2)
                         .link("www.google.co.kr")
                         .title("hello2")
                         .content("yeah")
                         .isPublic(false)
                         .build();
        
        Card card3 = Card.builder()
                .folder(folder2)
                .link("www.google.co.kr")
                .title("hello2")
                .content("year")
                .isPublic(true)
                .build();
    
        Tag tag = Tag.builder()
                     .isPublic(false)
                     .name("spring")
                     .member(member)
                     .build();
        
        Tag tag2 = Tag.builder()
                .isPublic(true)
                .name("spring2")
                .member(member)
                .build();
        
        CardTag cardTag = CardTag.builder()
                                 .card(card)
                                 .tag(tag)
                                 .build();
        
        CardTag cardTag2 = CardTag.builder()
                .card(card2)
                .tag(tag)
                .build();
        
        CardTag cardTag3 = CardTag.builder()
                .card(card3)
                .tag(tag2)
                .build();
    
        entityManager.persist(member);
        entityManager.persist(folder);
        entityManager.persist(folder2);
        entityManager.persist(card);
        entityManager.persist(card2);
        entityManager.persist(card3);
        entityManager.persist(tag);
        entityManager.persist(tag2);
        entityManager.persist(cardTag);
        entityManager.persist(cardTag2);
        entityManager.persist(cardTag3);
        entityManager.flush();
    }
    
    @Test
    @DisplayName("상세 조회")
    @WithMockMember(id = 1L, email = "marrin1101@naver.com")
    void CardExistFindingSuccess() throws Exception {
        
        mockMvc.perform(get("/api/cards/1"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.data").isNotEmpty());
    }
    
    @Test
    @DisplayName("태그 아이디로 사용자의 카드 목록 조회")
    @WithMockMember(id = 1L, email = "marrin1101@naver.com")
    void findCardsByTagIdSuccess() throws Exception {
        
        mockMvc.perform(get("/api/cards/tag/1?page=0&size=2"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.data").isNotEmpty());
    }
    
    @Test
    @DisplayName("태그 아이디로 isPublic인 카드 목록 조회 성공")
    @WithMockMember(id = 1L, email = "marrin1101@naver.com")
    void findIsPublicCardsByTagIdSuccess() throws Exception {

        mockMvc.perform(get("/api/cards/package/2?page=0&size=10"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.data").isNotEmpty());
    }
    
    @Test
    @DisplayName("폴더 아이디로 사용자의 카드 목록 조회 성공")
    @WithMockMember(id = 1L, email = "marrin1101@naver.com")
    void findCardsByFolderIdSuccess() throws Exception {
        mockMvc.perform(get("/api/cards/folder/2?findDeep=true"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.data").isNotEmpty());
    }
    
    @Test
    @DisplayName("사용자의 모든 카드 목록 조회 성공")
    @WithMockMember(id = 1L, email = "marrin1101@naver.com")
    void findCardsByMemberIdSuccess() throws Exception {
        mockMvc.perform(get("/api/cards/all?page=0&size=2"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.data").isNotEmpty());
    }
}
