package com.wnis.linkyway.controller.card;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.io.AddCardResponse;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.card.io.CopyCardsRequest;
import com.wnis.linkyway.dto.card.io.CopyPackageCardsRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.service.card.CardService;
import com.wnis.linkyway.utils.ResponseBodyMatchers;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CardController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class) })
public class CardControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();
    

    @MockBean
    private CardService cardService;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    WebApplicationContext ctx;
    
    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .alwaysDo(print())
                                 .build();
    }

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

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    void addCardSuccess() throws Exception {
        // given
        CardRequest addCardRequest = makeCardRequest();
        AddCardResponse addCardResponse = AddCardResponse.builder()
                                                         .cardId(1L)
                                                         .build();
        doReturn(addCardResponse).when(cardService)
                                 .addCard(any(), any(CardRequest.class));
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/cards").contentType("application/json")
                                                                        .content(objectMapper.writeValueAsString(addCardRequest)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                           .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                          // ResponseBodyMatcher
                                                                          .containsPropertiesAsJson(Response.class)) // method
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
                                       .isPublic(true)
                                       .build();
        }

        @Test
        @DisplayName("상세 조회 성공: 올바른 URL")
        void CardExistFindingSuccess() throws Exception {
            // given
            doReturn(cardResponse).when(cardService)
                                  .findCardByCardId(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/" + cardId).contentType("application/json")
                                                                                     .content(objectMapper.writeValueAsString(cardResponse)));

            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(ResponseBodyMatchers.responseBody()
                                                                              .containsPropertiesAsJson(Response.class))
                                               .andReturn();
            verify(cardService).findCardByCardId(any(), any());
        }

        @Test
        @DisplayName("상세 조회 실패: 잘못된 URL")
        void CardNotExistFindingFail() throws Exception {
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

        private Long cardId = 1000L;
        private CardRequest cardRequest;

        @BeforeEach
        void setCard() {
            cardRequest = makeCardRequest();
        }

        @Test
        @DisplayName("카드 수정 성공: 올바른 URL")
        void updateCardSuccess() throws Exception {
            // given
            CardRequest cardRequest = makeCardRequest();
            doReturn(cardId).when(cardService)
                            .updateCard(any(), any(), any(CardRequest.class));
            // when
            ResultActions resultActions = mockMvc.perform(put("/api/cards/" + cardId).contentType("application/json")
                                                                                     .content(objectMapper.writeValueAsString(cardRequest)));
            // then
            resultActions.andExpect(status().isOk())
                         .andExpect(ResponseBodyMatchers.responseBody()
                                                        .containsPropertiesAsJson(Response.class))
                         .andReturn();

            verify(cardService).updateCard(any(), any(), any(CardRequest.class));
        }

        @Test
        @DisplayName("카드 수정 실패: 잘못된 URL")
        void updateCardFail() throws Exception {
            // given
            CardRequest cardRequest = makeCardRequest();
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
        private Long cardId = 1000L;
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
                     .findCardsByTagId(any(), any(), anyLong(), any());
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



        @Test
        @DisplayName("폴더 아이디로 사용자의 카드 목록 조회 성공")
        void findCardsByFolderIdSuccess() throws Exception {
            // given
            lenient().doReturn(cardResponses)
                     .when(cardService)
                     .findCardsByFolderId(any(), anyLong(), anyLong(), any(Boolean.class), any());
            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/folder/"
                    + folderId).param("findDeep", String.valueOf(true))
                               .contentType("application/json")
                               .content(objectMapper.writeValueAsString(cardResponses)));
            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                              // ResponseBodyMatcher
                                                                              .containsPropertiesAsJson(Response.class)) // method
                                               .andReturn();
        }

        @Test
        @DisplayName("사용자의 모든 카드 목록 조회 성공")
        void findCardsByMemberIdSuccess() throws Exception {
            // given
            lenient().doReturn(cardResponses)
                     .when(cardService)
                     .findCardsByMemberId(any(), anyLong(), any());
            // when
            ResultActions resultActions = mockMvc.perform(get("/api/cards/all").contentType("application/json")
                                                                               .content(objectMapper.writeValueAsString(cardResponses)));
            // then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                               .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                              // ResponseBodyMatcher
                                                                              .containsPropertiesAsJson(Response.class)) // method
                                               .andReturn();
        }
    }

    @Test
    @DisplayName("패키지 목록(태그 내부 카드 목록) 복사 성공") // not done
    void copyCardsInPackageSuccess() throws Exception {
        // given
        List<CopyCardsRequest> copyCardsRequests = new ArrayList<CopyCardsRequest>(
                Arrays.asList(CopyCardsRequest.builder()
                                              .build(),
                              CopyCardsRequest.builder()
                                              .build()));
        CopyPackageCardsRequest copyPackageCardsRequest = CopyPackageCardsRequest.builder()
                                                                                 .tagId(1L)
                                                                                 .folderId(10L)
                                                                                 .isPublic(false)
                                                                                 .copyCardsRequestList(copyCardsRequests)
                                                                                 .build();
        int copyCardsSize = copyPackageCardsRequest.getCopyCardsRequestList()
                                                   .size();
        doReturn(copyCardsSize)
                 .when(cardService)
                 .copyCardsInPackage(any(CopyPackageCardsRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/cards/package/copy").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(copyPackageCardsRequest)));
        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                                           .andExpect(ResponseBodyMatchers.responseBody() // create
                                                                          // ResponseBodyMatcher
                                                                          .containsPropertiesAsJson(Response.class)) // method
                                           .andReturn();
    }
}
