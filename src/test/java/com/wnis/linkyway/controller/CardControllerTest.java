package com.wnis.linkyway.controller;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.exception.error.ErrorResponse;
import com.wnis.linkyway.service.CardService;
import com.wnis.linkyway.utils.ResponseBodyMatchers;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @InjectMocks
    private CardController cardController;

    @Mock
    private CardService cardService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @DisplayName("카드(북마크) 생성 성공")
    @Test
    void addCardSuccess() throws Exception {
        // given
        CardRequest addCardRequest = CardRequest.builder()
                                                .link("https://www.naver.com/")
                                                .title("title1")
                                                .content("content1")
                                                .shareable(true)
                                                .folderId(1L)
                                                .build();
        Long addCardResponse = 3L;

        doReturn(addCardResponse).when(cardService)
                                 .addCard(Mockito.any(CardRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/tags").contentType("application/json")
                                 .content(objectMapper.writeValueAsString(
                                         addCardRequest)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                           .andExpect(
                                                   ResponseBodyMatchers.responseBody() // create
                                                                       // ResponseBodyMatcher
                                                                       .containsPropertiesAsJson(
                                                                               ErrorResponse.class)) // method
                                           .andReturn();
    }

}
