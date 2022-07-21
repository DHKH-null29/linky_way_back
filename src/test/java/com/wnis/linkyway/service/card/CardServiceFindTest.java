package com.wnis.linkyway.service.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wnis.linkyway.dto.card.CardDto;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.cardtag.CardTagDto;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.repository.card.CardRepositoryCustom;
import com.wnis.linkyway.repository.cardtag.CardTagRepository;
import com.wnis.linkyway.repository.cardtag.CardTagRepositoryCustom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

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

    @Mock
    private CardTagRepositoryCustom cardTagRepositoryCustom;

    @Mock
    private CardRepositoryCustom cardRepositoryCustom;
    private Member member;
    private Folder folder1;
    private List<Card> cardList;
    private Tag tag1, tag2;
    private List<CardTagDto> cardTagDtoList;

    private List<CardDto> cardDtoList;

    @BeforeEach
    void setUp() {
        // given
        member = new Member(1L, "sssee", "a!aA212341", "maee@naver.com");

        folder1 = new Folder(10L, "f1", 1L, null, member);

        tag1 = new Tag(101L, "t1", true, member);
        tag2 = new Tag(102L, "t2", false, member);

        Card card1 = new Card(1001L, "https://www.naver.com/", "title1", "content1", true, folder1);
        Card card2 = new Card(1002L, "https://www.daum.net/", "title2", "content2", true, folder1);

        cardList = new ArrayList<Card>(Arrays.asList(card1, card2));
        cardTagDtoList = new ArrayList<>();
        CardTagDto cardTagDto = CardTagDto.builder()
            .cardId(1L)
            .tagId(1L)
            .tagName("hello")
            .content("asd")
            .build();
        cardTagDtoList.add(cardTagDto);
        cardDtoList = new ArrayList<>();
        CardDto cardDto = CardDto.builder()
            .id(1L)
            .title("h")
            .link("hello")
            .content("hello")
            .folderId(1L)
            .isPublic(false)
            .build();
        cardDtoList.add(cardDto);
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
            lenient().doReturn(cardDtoList)
                .when(cardRepositoryCustom)
                .findAllCardByTadId(any(), any(), any());

            // when
            List<CardResponse> cardResponses = cardService.findCardsByTagId(null, member.getId(),
                tag1.getId(), PageRequest.of(0, 200));
            System.out.println(cardResponses);
            // then
            assertThat(cardResponses).size()
                .isEqualTo(cardDtoList.size());
            for (int index = 0; index < cardResponses.size(); index++) {
                CardResponse cardResponse = cardResponses.get(index);
                CardDto card = cardDtoList.get(index);

                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolderId());
            }

            // verify
            verify(tagRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("카드 목록 조회 실패: 존재하지 않는 태그 또는 해당 사용자의 태그가 아님")
        void FindingCardsFailBecauseTagDoesNotExistOrNotBelongsToUser() throws Exception {
            // when
            doReturn(Optional.empty()).when(tagRepository)
                .findByIdAndMemberId(anyLong(), anyLong());
            // then
            assertThrows(ResourceConflictException.class,
                () -> cardService.findCardsByTagId(null, 2L, 10000L, PageRequest.of(0, 200)));

            // verify
            verify(tagRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
        }
    }


    @Nested
    @DisplayName("카드 목록 조회 : 폴더 아이디로 조회")
    class findCardsByFolderId {

//        @Test
//        @DisplayName("카드 목록 조회 성공: 해당 폴더만")
//        void FindingCardsSuccessWhenFindDeepIsFalse() throws Exception {
//            // given
//            Optional<Folder> folder = Optional.of(folder1);
//            lenient().doReturn(folder)
//                     .when(folderRepository)
//                     .findByIdAndMemberId(anyLong(), anyLong());
//
//            lenient().doReturn(cardDtoList)
//                .when(cardRepositoryCustom)
//                .findAllCardByFolderId(any(), any(), any());
//
//            lenient().doReturn(cardDtoList)
//                .when(cardRepositoryCustom)
//                .findAllCardByFolderIds(any(), any(), any());
//            // when
//            List<CardResponse> cardResponses = cardService.findCardsByFolderId(any(), member.getId(), folder1.getId(), false, PageRequest.of(0, 10));
//
//            // then
//            for (int index = 0; index < cardResponses.size(); index++) {
//                CardResponse cardResponse = cardResponses.get(index);
//                Card card = cardList.get(index);
//
//                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
//                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
//                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
//                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
//                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolder()
//                                                                     .getId());
//            }
//
//            // verify
//            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
//            verify(cardRepository, times(1)).findCardsByFolderId(anyLong(), any());
//            verify(cardRepository, times(0)).findDeepFoldersCardsByFolderId(anyLong());
//        }
//
//        @Test
//        @DisplayName("카드 목록 조회 성공: 하위 폴더까지")
//        void FindingCardsSuccessWhenFindDeepIsTrue() throws Exception {
//            // given
//            Optional<Folder> folder = Optional.of(folder1);
//            lenient().doReturn(folder)
//                     .when(folderRepository)
//                     .findByIdAndMemberId(anyLong(), anyLong());
//            lenient().doReturn(cardList)
//                    .when(cardRepository)
//                    .findAllInFolderIds(any(), any());
//            // when
//            List<CardResponse> cardResponses = cardService.findCardsByFolderId(any(), member.getId(), folder1.getId(), true, PageRequest.of(0, 200));
//
//            // then
//            for (int index = 0; index < cardResponses.size(); index++) {
//                CardResponse cardResponse = cardResponses.get(index);
//                Card card = cardList.get(index);
//
//                assertThat(cardResponse.getCardId()).isEqualTo(card.getId());
//                assertThat(cardResponse.getTitle()).isEqualTo(card.getTitle());
//                assertThat(cardResponse.getContent()).isEqualTo(card.getContent());
//                assertThat(cardResponse.getIsPublic()).isEqualTo(card.getIsPublic());
//                assertThat(cardResponse.getFolderId()).isEqualTo(card.getFolder()
//                                                                     .getId());
//            }
//
//            // verify
//            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
//            verify(cardRepository, times(0)).findCardsByFolderId(anyLong(), any());
//            verify(cardRepository, times(1)).findAllInFolderIds(any(), any());
//        }

        @Test
        @DisplayName("카드 목록 조회 실패: 존재하지 않는 폴더 또는 해당 사용자의 폴더가 아님")
        void FindingCardsFailBecauseFolderDoesNotExistOrNotBelongsToUser() throws Exception {
            // when
            doReturn(Optional.empty()).when(folderRepository)
                .findByIdAndMemberId(anyLong(), anyLong());
            // then
            assertThrows(ResourceConflictException.class,
                () -> cardService.findCardsByFolderId(null, 100L, 1L, true,
                    PageRequest.of(0, 200)));

            // verify
            verify(folderRepository, times(1)).findByIdAndMemberId(anyLong(), anyLong());
            verify(cardRepository, times(0)).findCardsByFolderId(anyLong(), any());
            verify(cardRepository, times(0)).findDeepFoldersCardsByFolderId(anyLong());
        }
    }
}