package com.wnis.linkyway.controller.card;

import com.wnis.linkyway.entity.*;
import com.wnis.linkyway.security.testutils.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class PersonalSearchIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    EntityManager entityManager;

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
    @DisplayName("개인 검색 테스트")
    class personalSearchTest {

        @BeforeEach
        void setUp() {
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
            
            Tag tag = Tag.builder()
                         .isPublic(false)
                         .name("spring")
                         .member(member)
                         .build();
            CardTag cardTag = CardTag.builder()
                                     .card(card)
                                     .tag(tag)
                                     .build();

            entityManager.persist(member);
            entityManager.persist(folder);
            entityManager.persist(folder2);
            entityManager.persist(card);
            entityManager.persist(card2);
            entityManager.persist(tag);
            entityManager.persist(cardTag);
            entityManager.flush();
        }

        @Test
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void shouldOkAndDataFormatTest() throws Exception {

            mockMvc.perform(get("/api/cards/personal/keyword?page=0&size=1").param("keyword", "hello"))
                   .andExpect(status().is(200))
                   .andExpect(jsonPath("$.data").isNotEmpty());
        }

    }
}
