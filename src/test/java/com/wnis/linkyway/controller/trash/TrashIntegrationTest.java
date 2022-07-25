package com.wnis.linkyway.controller.trash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.security.testutils.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/card-test.sql")
public class TrashIntegrationTest {
    
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    CardRepository cardRepository;
    
    @BeforeEach
    void setup() {
        
        // MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .apply(springSecurity())
                                 .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필터 추가
                                 .alwaysDo(print())
                                 .build();
    }
    
    @Test
    @WithMockMember
    void deleteCardTest() throws Exception {
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        mockMvc.perform(put("/api/trash?isDeleted=false").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockMember
    void findAllDeletedCard() throws Exception {
        List<Card> cardList = cardRepository.findAll();
        cardList.get(0).updateIsDeleted(true);
        cardList.get(1).updateIsDeleted(true);
        cardRepository.flush();
        
        mockMvc.perform(get("/api/trash?lastCardId=3&size=2"))
               .andExpect(status().isOk());
    }
    
}
