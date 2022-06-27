package com.wnis.linkyway.controller;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Sql("/sqltest/card-test.sql")
public class PackageIntegrationTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    WebApplicationContext ctx;
    
    @Autowired
    EntityManager em;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .alwaysDo(print())
                                 .build();
    }
    
    @Test
    @DisplayName("패키지 통합 테스트")
    void responseTest() throws Exception {
        Card card = em.find(Card.class, 1L);
        card.updateIsPublic(true);
    
        Tag tag = em.find(Tag.class, 1L);
        tag.updateIsPublic(true);
        em.flush();
        
        mockMvc.perform(get("/api/search/social?isLike=true").param("tagName", "ja"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$..memberId").isNotEmpty())
               .andExpect(jsonPath("$..nickname").isNotEmpty())
               .andExpect(jsonPath("$..numberOfCard").isNotEmpty())
               .andExpect(jsonPath("$..tagName").isNotEmpty())
               .andExpect(jsonPath("$..tagId").isNotEmpty());
    }
}
