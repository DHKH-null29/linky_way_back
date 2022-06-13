package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotModifyEmptyEntityException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.TagRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;
    
    @Mock
    private FolderRepository folderRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @Mock
    private CardTagRepository cardTagRepository;

    private final Long cardId = 3L;
    private final String link = "https://github.com/DHKH-null29/linky_way_back/issues/12";
    private final String title = "카드 조회";
    private final String content = "카드 조회 issue";
    private final boolean isPublic = true;
    private final Folder folder = null;

    private Card card() {
        Card newCard = Card.builder()
                           .link(link)
                           .title(title)
                           .content(content)
                           .isPublic(isPublic)
                           .isDeleted(false)
                           .folder(folder)
                           .build();
        newCard.setId(cardId);
        return newCard;
    }

    private CardRequest makeCardRequest() {
        return CardRequest.builder()
                          .link(link)
                          .title(title)
                          .content(content)
                          .isPublic(isPublic)
                          .folderId(1L)
                          .build();
    }

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        Card card = card();
        CardRequest cardRequest = makeCardRequest();
        List<Tag> tagList = new ArrayList<>();
        tagList.add(Tag.builder().build());
        
        BDDMockito.given(cardRepository.save(any(Card.class)))
                  .willReturn(card);
        
        doReturn(true).when(folderRepository).existsById(any());
        doReturn(tagList).when(tagRepository).findAllById(any());
        doReturn(CardTag.builder().build()).when(cardTagRepository).save(any());
        
        // when
        cardService.addCard(1L, cardRequest);
        
        // 북마크 추가시 카드 저장과 카드-태그 연관관계 저장 메서드 호출 검증
        verify(cardRepository).save(Mockito.any(Card.class));
        verify(cardTagRepository).save(any());
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
                                .findById(any());
            doReturn(new ArrayList<CardResponse>()).when(cardTagRepository).findAllTagResponseByCardId(any());
            // when
            CardResponse cardResponse = cardService.findCardByCardId(savedCard.getId());

            // then
            assertThat(cardResponse).isNotNull();
            assertEquals(cardId, cardResponse.getCardId());
            assertEquals(link, cardResponse.getLink());
            assertEquals(title, cardResponse.getTitle());
            assertEquals(content, cardResponse.getContent());
            assertEquals(isPublic, cardResponse.getIsPublic());
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
                                     .isPublic(!isPublic)
                                     .folderId(1L)
                                     .build();
        }

        @Test
        @DisplayName("카드 수정 성공: 카드가 존재함")
        void CardExistFindingSuccess() throws Exception {
            // 성공 테스트는 Mock으로 하는건 큰 의미가 없어 보이긴 함... 노가다가 많음..
            // 임의 mock으로 설정했지만 함수가 실행된다는 것만 검증됨..
            doReturn(Optional.of(savedCard)).when(cardRepository)
                                            .findById(any());
            
            doReturn(true).when(folderRepository).existsById(any());
            doReturn(Folder.builder().build()).when(folderRepository).getById(any());
            doReturn(card()).when(cardRepository).save(any());
            doReturn(new HashSet<Tag>()).when(cardTagRepository).findAllTagIdByCardId(any());
            doReturn(new ArrayList<Tag>()).when(tagRepository).findAllById(any());
            doReturn(new ArrayList<Long>()).when(cardTagRepository).findAllCardTagIdInTagSet(any());
            doNothing().when(cardTagRepository).deleteAllCardTagInIds(any());
            cardService.updateCard(1L,1L, cardRequest);
            // then
//            Assertions.assertNotNull(updatedCard);
        }

        @Test
        @DisplayName("카드 수정 실패: 카드가 없음")
        void CardNotExistFindingFail() throws Exception {
            // given
            doReturn(Optional.empty()).when(cardRepository)
                                      .findById(Mockito.anyLong());

            
            assertThatThrownBy(() -> {
                cardService.updateCard(1L, 1L, cardRequest);
            }).isInstanceOf(NotModifyEmptyEntityException.class);
            
        }
    }
}