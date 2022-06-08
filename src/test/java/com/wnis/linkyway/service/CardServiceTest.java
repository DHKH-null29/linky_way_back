package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.service.card.CardServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    private final Long cardId = 3L;
    private final String link = "https://github.com/DHKH-null29/linky_way_back/issues/12";
    private final String title = "카드 조회";
    private final String content = "카드 조회 issue";
    private final boolean shareable = true;
    private final Folder folder = null;

    private Card card() {
        Card newCard = Card.builder()
                           .link(link)
                           .title(title)
                           .content(content)
                           .shareable(shareable)
                           .folder(folder)
                           .build();
        newCard.setId(cardId);
        return newCard;
    }

    private CardRequest cardRequest() {
        return CardRequest.builder()
                          .link(link)
                          .title(title)
                          .content(content)
                          .shareable(shareable)
                          .folderId(1L)
                          .build();
    }

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        Card card = card();
        CardRequest cardRequest = cardRequest();
        BDDMockito.given(cardRepository.save(any(Card.class))).willReturn(card);

        // when
//        AddCardResponse addCardResponse = cardService.addCard(cardRequest);

        // then
//        assertThat(addCardResponse).isNotNull();
        verify(cardRepository).save(Mockito.any(Card.class));
    }

    @Nested
    @DisplayName("단일 북마크(카드) 상세 조회")
    class findCardByCardId {

        private Card savedCard;

        @BeforeEach
        void setCard() {
            Card card = card();
            BDDMockito.given(cardRepository.save(Mockito.any(Card.class)))
                      .willReturn(card);
            savedCard = cardRepository.save(card);
        }

        @Test
        @DisplayName("상세 조회 성공: 카드가 존재함")
        void CardExistFindingSuccess() throws Exception {
            // given
            Optional<Card> resultCard = Optional.of(savedCard);
            doReturn(resultCard).when(cardRepository)
                                .findById(savedCard.getId());

            // when
            CardResponse cardResponse = cardService.findCardByCardId(
                    savedCard.getId());

            // then
            assertThat(cardResponse).isNotNull();
            assertEquals(cardId, cardResponse.getCardId());
            assertEquals(link, cardResponse.getLink());
            assertEquals(title, cardResponse.getTitle());
            assertEquals(content, cardResponse.getContent());
            assertEquals(shareable, cardResponse.getShareable());
            verify(cardRepository).findById(Mockito.anyLong());
        }

        @Test
        @DisplayName("상세 조회 실패: 카드가 없음")
        void CardNotExistFindingFail() throws Exception {
            // when
            doReturn(Optional.empty()).when(cardRepository)
                                      .findById(Mockito.anyLong());

            // then
            Assertions.assertThrows(ResourceNotFoundException.class,
                    () -> cardService.findCardByCardId(Mockito.anyLong()));
            verify(cardRepository).findById(Mockito.anyLong());
        }
    }

    @Nested
    @DisplayName("카드(북마크) 수정")
    class updateCard {

        private Card savedCard;
        private CardRequest cardRequest;

        @BeforeEach
        void setCard() {
            Card card = card();
            BDDMockito.given(cardRepository.save(Mockito.any(Card.class)))
                      .willReturn(card);
            savedCard = cardRepository.save(card);

            cardRequest = CardRequest.builder()
                                     .link(link + 2)
                                     .title(title + 2)
                                     .content(content + 2)
                                     .shareable(!shareable)
                                     .folderId(1L)
                                     .build();
        }

        @Test
        @DisplayName("카드 수정 성공: 카드가 존재함")
        void CardExistFindingSuccess() throws Exception {
            // given
            doReturn(Optional.of(savedCard)).when(cardRepository)
                                            .findById(savedCard.getId());
            // when
//            Card updatedCard = cardService.updateCard(savedCard.getId(),
//                    cardRequest);
            // then
//            Assertions.assertNotNull(updatedCard);
        }

        @Test
        @DisplayName("카드 수정 실패: 카드가 없음")
        void CardNotExistFindingFail() throws Exception {
            // when
            doReturn(Optional.empty()).when(cardRepository)
                                      .findById(Mockito.anyLong());

            // then
//            Assertions.assertThrows(ResourceConflictException.class,
//                    () -> cardService.updateCard(savedCard.getId(),
//                            cardRequest));
            verify(cardRepository).findById(Mockito.anyLong());
        }
    }
}