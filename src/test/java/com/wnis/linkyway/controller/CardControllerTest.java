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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.service.card.CardService;
import com.wnis.linkyway.utils.ResponseBodyMatchers;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CardController cardController;

    @Mock
    private CardService cardService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @DisplayName("카드(북마크) 추가 성공")
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
        AddCardResponse addCardResponse = AddCardResponse.builder()
                                                         .cardId(3L)
                                                         .build();

        doReturn(addCardResponse).when(cardService)
                                 .addCard(Mockito.any(CardRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/cards").contentType("application/json")
                                  .content(objectMapper.writeValueAsString(
                                          addCardRequest)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isCreated())
                                           .andExpect(
                                                   ResponseBodyMatchers.responseBody() // create
                                                                       // ResponseBodyMatcher
                                                                       .containsPropertiesAsJson(
                                                                               Response.class)) // method
                                           .andReturn();
    }

}
