package com.wnis.linkyway.controller.card;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.wnis.linkyway.controller.CardController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import com.wnis.linkyway.dto.card.CopyCardsRequest;
import com.wnis.linkyway.dto.card.CopyPackageCardsRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
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
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                                 .build();
    }

    @DisplayName("카드(북마크) 추가 성공")
    private CardRequest makeCardRequest() {
        Set<Long> tagIdSet = new HashSet<>(Arrays.asList(1L, 2L));
        return CardRequest.builder()
                          .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                          .title("카드 조회")
                          .content("카드 조회 issue")
                          .isPublic(true)
                          .folderId(1L)
                          .tagIdSet(tagIdSet)
                          .build();
    }

    private Long cardId = 100L;

    private CardResponse makeCardResponse() {
        TagResponse tagResponse1 = TagResponse.builder()
                                              .tagName("t1")
                                              .isPublic(true)
                                              .build();
        TagResponse tagResponse2 = TagResponse.builder()
                                              .tagName("t2")
                                              .isPublic(false)
                                              .build();
        return CardResponse.builder()
                           .cardId(cardId)
                           .link("https://www.naver.com/")
                           .title("title1")
                           .content("content1")
                           .isPublic(true)
                           .tags(Arrays.asList(tagResponse1, tagResponse2))
                           .build();
    }

    @Test
    void addCardSuccess() throws Exception {
        // given
        CardRequest addCardRequest = CardRequest.builder()
                                                .link("https://www.naver.com/")
                                                .title("title1")
                                                .content("content1")
                                                .isPublic(true)
                                                .folderId(1L)
                                                .build();
        AddCardResponse addCardResponse = AddCardResponse.builder()
                                                         .cardId(3L)
                                                         .build();

//        doReturn(addCardResponse).when(cardService)
//                                 .addCard(Mockito.any(CardRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/cards").contentType("application/json")
                                                                        .content(objectMapper.writeValueAsString(addCardRequest)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isCreated())
                                           .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                          // ResponseBodyMatcher
                                                                          .containsPropertiesAsJson(Response.class)) // method
                                           .andReturn();
    }

    @Nested
    @DisplayName("단일 북마크(카드) 상세 조회")
    class findCardByCardId {

        @Test
        @DisplayName("상세 조회 성공: 올바른 URL")
        void CardExistFindingSuccess() throws Exception {
            // given
            CardResponse cardResponse = makeCardResponse();
            doReturn(cardResponse).when(cardService)
                                  .findCardByCardId(any());
            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/" + cardId).contentType("application/json")
                                                                                     .content(objectMapper.writeValueAsString(cardResponse)));
            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(ResponseBodyMatchers.responseBody()
                                                                              .containsPropertiesAsJson(Response.class))
                                               .andReturn();
            verify(cardService).findCardByCardId(any());
        }

        @Test
        @DisplayName("상세 조회 실패: 잘못된 URL")
        void CardNotExistFindingFail() throws Exception {
            // given
            CardResponse cardResponse = makeCardResponse();
            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(cardResponse)));
            // then
            resultActions.andExpect(status().isMethodNotAllowed())
                         .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException())
                                                                           .getClass())
                                                        .isEqualTo(HttpRequestMethodNotSupportedException.class))
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
                                       .isPublic(true)
                                       .build();
            cardRequest = CardRequest.builder()
                                     .link("https://www.daum.net/")
                                     .title("title2")
                                     .content("content2")
                                     .isPublic(false)
                                     .folderId(1L)
                                     .build();
        }

        @Test
        @DisplayName("카드 수정 성공: 올바른 URL")
        void updateCardSuccess() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(put("/api/cards/" + cardId).contentType("application/json")
                                                                                     .content(objectMapper.writeValueAsString(cardRequest)));

            // then
            resultActions.andExpect(status().isOk())
                         .andExpect(ResponseBodyMatchers.responseBody()
                                                        .containsPropertiesAsJson(Response.class))
                         .andReturn();

//            verify(cardService).updateCard(Mockito.anyLong(),
//                    any(CardRequest.class));
        }

        @Test
        @DisplayName("카드 수정 실패: 잘못된 URL")
        void updateCardFail() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/cards/").contentType("application/json")
                                                                              .content(objectMapper.writeValueAsString(cardRequest)));

            // then
            resultActions.andExpect(status().isMethodNotAllowed())
                         .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException())
                                                                           .getClass())
                                                        .isEqualTo(HttpRequestMethodNotSupportedException.class))
                         .andReturn();
        }
    }
    @Nested
    @DisplayName("카드 목록 조회")
    class findCards {

        private List<CardResponse> cardResponses = new ArrayList<CardResponse>();
        private Long tagId1 = 1L;
        private Long tagId2 = 2L;
        private Long folderId = 10L;

        @BeforeEach
        void initCardList() {
            TagResponse tagResponse1 = TagResponse.builder()
                                                  .tagId(tagId1)
                                                  .tagName("t1")
                                                  .isPublic(true)
                                                  .build();
            TagResponse tagResponse2 = TagResponse.builder()
                                                  .tagId(tagId2)
                                                  .tagName("t2")
                                                  .isPublic(false)
                                                  .build();
            cardResponses = Arrays.asList(CardResponse.builder()
                                                      .cardId(cardId)
                                                      .link("https://www.naver.com/")
                                                      .title("title1")
                                                      .content("content1")
                                                      .isPublic(true)
                                                      .folderId(folderId)
                                                      .tags(Arrays.asList(tagResponse1, tagResponse2))
                                                      .build(),
                                          CardResponse.builder()
                                                      .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                                                      .title("카드 조회")
                                                      .content("카드 조회 issue")
                                                      .isPublic(true)
                                                      .folderId(folderId)
                                                      .tags(Arrays.asList(tagResponse1))
                                                      .build());
        }

        @Test
        @DisplayName("태그 아이디로 사용자의 카드 목록 조회 성공")
        void findCardsByTagIdSuccess() throws Exception {
            // given
            lenient().doReturn(cardResponses)
                     .when(cardService)
                     .findCardsByTagId(any(), anyLong());
            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/tag/"
                    + tagId1).contentType("application/json")
                             .content(objectMapper.writeValueAsString(cardResponses)));

            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                              // ResponseBodyMatcher
                                                                              .containsPropertiesAsJson(Response.class)) // method
                                               .andReturn();
        }
    }
}
