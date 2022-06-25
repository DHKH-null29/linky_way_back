package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.CopyCardsRequest;
import com.wnis.linkyway.dto.card.CopyPackageCardsRequest;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
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
    private MemberRepository memberRepository;

    @Mock
    private CardTagRepository cardTagRepository;

    private final Long cardId = 1001L;
    private final String link = "https://github.com/DHKH-null29/linky_way_back/issues/12";
    private final String title = "카드 조회";
    private final String content = "카드 조회 issue";
    private final boolean isPublic = true;

    Member member = new Member(1L, "sssee", "a!aA212341", "maee@naver.com");

    Optional<Folder> folder1 = Optional.of(new Folder(11L, "f1", 1L, null, member));
    Optional<Folder> folder2 = Optional.of(new Folder(12L, "f2", 2L, folder1.get(), member));

    Tag tag1 = new Tag(101L, "t1", true, member);
    Tag tag2 = new Tag(102L, "t2", false, member);

    private Card makeCard() {
        return new Card(cardId, link, title, content, isPublic, folder1.get());
    }

    private CardRequest makeCardRequest() {
        Set<Long> tagSet = new HashSet<>(Arrays.asList(tag1.getId(), tag2.getId()));
        return CardRequest.builder()
                          .link(link)
                          .title(title)
                          .content(content)
                          .isPublic(isPublic)
                          .folderId(folder1.get()
                                           .getId())
                          .tagIdSet(tagSet)
                          .build();
    }

    @Nested
    @DisplayName("카드(북마크) 추가")
    class addCard {

        @Test
        @DisplayName("카드 추가 성공")
        public void addCardSuccess() {
            // given
            Card card = makeCard();
            CardRequest cardRequest = makeCardRequest();
            List<Tag> tagList = Arrays.asList(tag1, tag2);
            CardTag cardTag1 = CardTag.builder()
                                      .card(card)
                                      .tag(tag1)
                                      .build();
            CardTag cardTag2 = CardTag.builder()
                                      .card(card)
                                      .tag(tag2)
                                      .build();
            List<CardTag> savedCardTagList = Arrays.asList(cardTag1, cardTag2);

            doReturn(folder1).when(folderRepository)
                             .findByIdAndMemberId(anyLong(), anyLong());
            doReturn(tagList).when(tagRepository)
                             .findAllById(anySet());
            doReturn(card).when(cardRepository)
                          .save(any(Card.class));
            doReturn(savedCardTagList).when(cardTagRepository)
                                      .saveAll(anyList());

            // when
            cardService.addCard(1L, cardRequest);

            // verify
            verify(folderRepository).findByIdAndMemberId(anyLong(), anyLong());
            verify(tagRepository).findAllById(anySet());
            verify(cardRepository).save(Mockito.any(Card.class));
            verify(cardTagRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("카드 추가 실패: 사용자가 일치하지 않음")
        public void addCardFailBecauseUserIsNotCorrect() {

        }

        @Test
        @DisplayName("카드 추가 실패: 폴더가 존재하지 않음")
        public void addCardFailBecauseFolderIsNotExist() {

        }

        @Test
        @DisplayName("카드 추가 실패: 태그가 존재하지 않음")
        public void addCardFailBecauseTagIsNotExist() {

        }
    }

    @Nested
    @DisplayName("단일 북마크(카드) 상세 조회")
    class findCardByCardId {

        @Test
        @DisplayName("상세 조회 성공: 카드가 존재함")
        void CardExistFindingSuccess() throws Exception {
            // given
            Optional<Card> savedCard = Optional.of(makeCard());
            doReturn(savedCard).when(cardRepository)
                               .findById(any());
            // when
            CardResponse cardResponse = cardService.findCardByCardId(savedCard.get()
                                                                              .getId());

            // then
            assertThat(cardResponse).isNotNull();
            assertEquals(cardId, cardResponse.getCardId());
            assertEquals(link, cardResponse.getLink());
            assertEquals(title, cardResponse.getTitle());
            assertEquals(content, cardResponse.getContent());
            assertEquals(isPublic, cardResponse.getIsPublic());

            // verify
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

        private Optional<Card> oldCard;
        private CardRequest newCardRequest;
        private List<Tag> newTagList;
        private List<CardTag> savedCardTagList;

        @BeforeEach
        void setCard() {
            oldCard = Optional.of(makeCard());
            Tag tag3 = new Tag(103L, "t3", false, member);
            Set<Long> tagSet = new HashSet<>(Arrays.asList(tag3.getId()));
            newCardRequest = CardRequest.builder()
                                        .link("https://www.naver.com/")
                                        .title("네이버")
                                        .content("검색 서비스")
                                        .isPublic(false)
                                        .folderId(12L)
                                        .tagIdSet(tagSet)
                                        .build();
            newTagList = Arrays.asList(tag3);
            Card savedCard = new Card(1L, "https://www.naver.com/", "네이버", "검색 서비스", false, folder2.get());
            CardTag cardTag = CardTag.builder()
                                     .card(savedCard)
                                     .tag(tag3)
                                     .build();
            savedCardTagList = Arrays.asList(cardTag);
        }

        @Test
        @DisplayName("카드 수정 성공: 모든 정보가 다름")
        void updateCardSuccess() throws Exception {
            // given
            doReturn(oldCard).when(cardRepository)
                             .findById(anyLong());
            doReturn(folder2).when(folderRepository)
                             .findByIdAndMemberId(anyLong(), anyLong());
            doReturn(newTagList).when(tagRepository)
                                .findAllById(anySet());
            doReturn(savedCardTagList).when(cardTagRepository)
                                      .saveAll(anyList());

            // when
            cardService.updateCard(1L, 1L, newCardRequest);

            // verify
            verify(cardRepository).findById(anyLong());
            verify(folderRepository).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardTagRepository).deleteAll(anyList());
            verify(tagRepository).findAllById(anySet());
            verify(cardTagRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("패키지 내부 카드 모두 복사")
    class copyCardsInPackage {

        CopyPackageCardsRequest copyPackageCardsRequest;
        List<Card> savedCardList;
        List<CardTag> savedCardTagList;

        @BeforeEach
        void setCopyPackageCardsRequest() {
            List<CopyCardsRequest> copyCardsRequestList = Arrays.asList(CopyCardsRequest.builder()
                                                                                        .link(link)
                                                                                        .title(title)
                                                                                        .content(content)
                                                                                        .build(),
                                                                        CopyCardsRequest.builder()
                                                                                        .link(link + 123)
                                                                                        .title(title + 123)
                                                                                        .content(content + 123)
                                                                                        .build());
            copyPackageCardsRequest = CopyPackageCardsRequest.builder()
                                                             .tagId(tag1.getId())
                                                             .isPublic(isPublic)
                                                             .copyCardsRequestList(copyCardsRequestList)
                                                             .build();
            savedCardList = Arrays.asList(new Card(cardId, link, title, content, isPublic, folder1.get()), new Card(
                    cardId + 1, link + 123, title + 123, content + 123, isPublic, folder1.get()));
        
            savedCardTagList = Arrays.asList(CardTag.builder()
                                             .card(savedCardList.get(0))
                                             .tag(tag1)
                                             .build(),
                                             CardTag.builder()
                                             .card(savedCardList.get(1))
                                             .tag(tag1)
                                             .build());
        
        }

        @Test
        @DisplayName("패키지의 모든 카드 복사 성공")
        void copyCardsInPackageSuccess() throws Exception {
            // given
            Optional<Tag> tag = Optional.of(tag1);
            doReturn(folder1).when(folderRepository)
                             .findById(any());
            doReturn(tag).when(tagRepository)
                          .findById(anyLong());
            doReturn(savedCardList).when(cardRepository)
                                   .saveAll(anyList());
            doReturn(savedCardTagList).when(cardTagRepository)
                                      .saveAll(anyList());

            // when
            cardService.copyCardsInPackage(copyPackageCardsRequest);

            // verify
            verify(folderRepository).findById(any());
            verify(tagRepository).findById(anyLong());
            verify(cardRepository).saveAll(anyList());
            verify(cardTagRepository).saveAll(anyList());
        }
    }
}