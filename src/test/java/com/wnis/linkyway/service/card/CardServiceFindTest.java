package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.SocialCardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotAccessableException;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceFindTest {

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

    private Member member;
    private Folder folder1;
    private List<Card> cardList;
    private Tag tag1, tag2;

    @BeforeEach
    void setUp() {
        // given
        member = Member.builder()
                       .email("maee@naver.com")
                       .nickname("sssee")
                       .password("a!aA212341")
                       .build();
        member.setId(1L);

        folder1 = Folder.builder()
                        .member(member)
                        .depth(1L)
                        .name("f")
                        .build();
        folder1.setId(10L);

        tag1 = Tag.builder()
                  .name("t1")
                  .isPublic(true)
                  .build();
        tag1.setId(100L);

        tag2 = Tag.builder()
                  .name("t2")
                  .isPublic(false)
                  .build();
        tag2.setId(101L);

        Card card1 = Card.builder()
                         .link("https://www.naver.com/")
                         .title("title1")
                         .content("content1")
                         .isPublic(true)
                         .folder(folder1)
                         .build();
        card1.setId(1000L);

        Card card2 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(true)
                         .folder(folder1)
                         .build();
        card2.setId(1001L);
        cardList = new ArrayList<Card>(Arrays.asList(card1, card2));
    }

    @Nested
    @DisplayName("카드 목록 조회 : 태그 아이디로 조회")
    class findCardsByTagId {

        @Test
        @DisplayName("카드 목록 조회 성공")
        void FindingCardsSuccess() throws Exception {
            // given
            Optional<Tag> tag = Optional.of(tag1);
            lenient().doReturn(tag)
                     .when(tagRepository)
                     .findByIdAndMemberId(anyLong(), anyLong());
            lenient().doReturn(cardList)
                     .when(cardRepository)
                     .findCardsByTagId(anyLong());

            // when
            List<CardResponse> cardResponses = cardService.findCardsByTagId(member.getId(), tag1.getId());
            // then
            assertThat(cardResponses).size()
                                     .isEqualTo(cardList.size());
            for (int index = 0; index < cardResponses.size(); index++) {
                CardResponse cardResponse = cardResponses.get(index);
                Card card = cardList.get(index);

                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolder()
                                                                     .getId());
            }

            // verify
            verify(tagRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardRepository, times(1)).findCardsByTagId(anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 실패: 존재하지 않는 태그 또는 해당 사용자의 태그가 아님")
        void FindingCardsFailBecauseTagDoesNotExistOrNotBelongsToUser() throws Exception {
            // when
            doReturn(Optional.empty()).when(tagRepository)
                                      .findByIdAndMemberId(anyLong(), anyLong());
            // then
            assertThrows(ResourceConflictException.class, () -> cardService.findCardsByTagId(2L, 10000L));

            // verify
            verify(tagRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("카드 목록 조회 : 태그 아이디로 isPublic=true인 카드 조회")
    class findIsPublicCardsByTagId {
        @Test
        @DisplayName("카드 목록 조회 성공")
        void FindingCardsSuccess() throws Exception {
            // given
            Optional<Tag> tag = Optional.of(tag1);
            lenient().doReturn(tag)
                     .when(tagRepository)
                     .findById(anyLong());
            lenient().doReturn(cardList)
                     .when(cardRepository)
                     .findIsPublicCardsByTagId(anyLong());
            // when
            List<SocialCardResponse> socialCardResponses = cardService.findIsPublicCardsByTagId(tag1.getId());

            // then
            for (int index = 0; index < socialCardResponses.size(); index++) {
                SocialCardResponse response = socialCardResponses.get(index);
                Card card = cardList.get(index);

                assertThat(response.getCardId()).isEqualTo(card.getId());
                assertThat(response.getLink()).isEqualTo(card.getLink());
                assertThat(response.getTitle()).isEqualTo(card.getTitle());
                assertThat(response.getContent()).isEqualTo(card.getContent());
                assertThat(response.getIsPublic()).isEqualTo(card.getIsPublic());
            }

            // verify
            verify(tagRepository, times(1)).findById(anyLong());
            verify(cardRepository, times(1)).findIsPublicCardsByTagId(anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 실패: 존재하지 않는 태그")
        void FindingCardsFailBecauseTagDoesNotExist() throws Exception {
            // when
            doReturn(Optional.empty()).when(tagRepository)
                                      .findById(anyLong());
            // then
            assertThrows(ResourceConflictException.class, () -> cardService.findIsPublicCardsByTagId(10000L));

            // verify
            verify(tagRepository, times(1)).findById(anyLong());
            verify(cardRepository, times(0)).findIsPublicCardsByTagId(anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 실패: 소셜 공유가 허용되지 않은 태그")
        void FindingCardsFailBecauseTagIsNotPublic() throws Exception {
            // when
            Optional<Tag> tag = Optional.of(tag2);
            doReturn(tag).when(tagRepository)
                         .findById(anyLong());
            // then
            assertThrows(NotAccessableException.class, () -> cardService.findIsPublicCardsByTagId(tag2.getId()));

            // verify
            verify(tagRepository, times(1)).findById(anyLong());
            verify(cardRepository, times(0)).findIsPublicCardsByTagId(anyLong());
        }
    }

    @Nested
    @DisplayName("카드 목록 조회 : 폴더 아이디로 조회")
    class findCardsByFolderId {

        @Test
        @DisplayName("카드 목록 조회 성공: 해당 폴더만")
        void FindingCardsSuccessWhenFindDeepIsFalse() throws Exception {
            // given
            Optional<Folder> folder = Optional.of(folder1);
            lenient().doReturn(folder)
                     .when(folderRepository)
                     .findByIdAndMemberId(anyLong(), anyLong());
            lenient().doReturn(cardList)
                     .when(cardRepository)
                     .findCardsByFolderId(anyLong());
            // when
            List<CardResponse> cardResponses = cardService.findCardsByFolderId(member.getId(), folder1.getId(), false);

            // then
            for (int index = 0; index < cardResponses.size(); index++) {
                CardResponse cardResponse = cardResponses.get(index);
                Card card = cardList.get(index);

                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolder()
                                                                     .getId());
            }

            // verify
            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardRepository, times(1)).findCardsByFolderId(anyLong());
            verify(cardRepository, times(0)).findDeepFoldersCardsByFolderId(anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 성공: 하위 폴더까지")
        void FindingCardsSuccessWhenFindDeepIsTrue() throws Exception {
            // given
            Optional<Folder> folder = Optional.of(folder1);
            lenient().doReturn(folder)
                     .when(folderRepository)
                     .findByIdAndMemberId(anyLong(), anyLong());
            lenient().doReturn(cardList)
                     .when(cardRepository)
                     .findDeepFoldersCardsByFolderId(anyLong());
            // when
            List<CardResponse> cardResponses = cardService.findCardsByFolderId(member.getId(), folder1.getId(), true);

            // then
            for (int index = 0; index < cardResponses.size(); index++) {
                CardResponse cardResponse = cardResponses.get(index);
                Card card = cardList.get(index);

                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolder()
                                                                     .getId());
            }

            // verify
            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardRepository, times(0)).findCardsByFolderId(anyLong());
            verify(cardRepository, times(1)).findDeepFoldersCardsByFolderId(anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 실패: 존재하지 않는 폴더 또는 해당 사용자의 폴더가 아님")
        void FindingCardsFailBecauseFolderDoesNotExistOrNotBelongsToUser() throws Exception {
            // when
            doReturn(Optional.empty()).when(folderRepository)
                                      .findByIdAndMemberId(anyLong(), anyLong());
            // then
            assertThrows(ResourceConflictException.class, () -> cardService.findCardsByFolderId(100L, 1L, true));
            // verify
            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardRepository, times(0)).findCardsByFolderId(anyLong());
            verify(cardRepository, times(0)).findDeepFoldersCardsByFolderId(anyLong());
        }
    }
}