package com.wnis.linkyway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
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

//        doReturn(addCardResponse).when(cardService)
//                                 .addCard(Mockito.any(CardRequest.class));

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

    @Nested
    @DisplayName("단일 북마크(카드) 상세 조회")
    class findCardByCardId {

        Long cardId;
        CardResponse cardResponse;

        @BeforeEach
        void setCard() {
            cardId = 3L;
            cardResponse = CardResponse.builder()
                                       .cardId(cardId)
                                       .link("https://www.naver.com/")
                                       .title("title1")
                                       .content("content1")
                                       .shareable(true)
                                       .build();
        }

        @Test
        @DisplayName("상세 조회 성공: 올바른 URL")
        void CardExistFindingSuccess() throws Exception {
            // given
            doReturn(cardResponse).when(cardService).findCardByCardId(any());

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/"
                    + cardId).contentType("application/json")
                             .content(objectMapper.writeValueAsString(
                                     cardResponse)));

            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(
                                                       ResponseBodyMatchers.responseBody()
                                                                           .containsPropertiesAsJson(
                                                                                   Response.class))
                                               .andReturn();
            verify(cardService).findCardByCardId(any());
        }

        @Test
        @DisplayName("상세 조회 실패: 잘못된 URL")
        void CardNotExistFindingFail() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/cards/").contentType("application/json")
                                      .content(objectMapper.writeValueAsString(
                                              cardResponse)));

            // then
            resultActions.andExpect(status().isMethodNotAllowed())
                         .andExpect(
                                 result -> Assertions.assertThat(
                                         Objects.requireNonNull(
                                                 result.getResolvedException())
                                                .getClass())
                                                     .isEqualTo(
                                                             HttpRequestMethodNotSupportedException.class))
                         .andReturn();
        }
    }

    @Nested
    @DisplayName("북마크(카드) 수정")
    class updateCard {

        Long cardId;
        CardResponse cardResponse;
        CardRequest cardRequest;

        @BeforeEach
        void setCard() {
            cardId = 3L;
            cardResponse = CardResponse.builder()
                                       .cardId(3L)
                                       .link("https://www.naver.com/")
                                       .title("title1")
                                       .content("content1")
                                       .shareable(true)
                                       .build();
            cardRequest = CardRequest.builder()
                                     .link("https://www.daum.net/")
                                     .title("title2")
                                     .content("content2")
                                     .shareable(false)
                                     .folderId(1L)
                                     .build();
        }

        @Test
        @DisplayName("카드 수정 성공: 올바른 URL")
        void updateCardSuccess() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(put("/api/cards/"
                    + cardId).contentType("application/json")
                             .content(objectMapper.writeValueAsString(
                                     cardRequest)));

            // then
            resultActions.andExpect(status().isOk())
                         .andExpect(
                                 ResponseBodyMatchers.responseBody()
                                                     .containsPropertiesAsJson(
                                                             Response.class))
                         .andReturn();

//            verify(cardService).updateCard(Mockito.anyLong(),
//                    any(CardRequest.class));
        }

        @Test
        @DisplayName("카드 수정 실패: 잘못된 URL")
        void updateCardFail() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/cards/").contentType("application/json")
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        cardRequest)));

            // then
            resultActions.andExpect(status().isMethodNotAllowed())
                         .andExpect(
                                 result -> Assertions.assertThat(
                                         Objects.requireNonNull(
                                                 result.getResolvedException())
                                                .getClass())
                                                     .isEqualTo(
                                                             HttpRequestMethodNotSupportedException.class))
                         .andReturn();
        }
    }
}
