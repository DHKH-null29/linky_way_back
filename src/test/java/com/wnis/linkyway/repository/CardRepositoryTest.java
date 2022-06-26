package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/card-test.sql")
public class CardRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CardRepositoryTest.class);

    @Autowired
    CardTagRepository cardTagRepository;

    @Autowired
    EntityManager em;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    private CardRepository cardRepository;

    private Folder folder;
    private Folder folder2;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                              .email("maee@naver.com")
                              .nickname("sssee")
                              .password("a!aA212341")
                              .build();

        folder = Folder.builder()
                       .member(member)
                       .depth(1L)
                       .name("f1")
                       .build();

        folder2 = Folder.builder()
                        .member(member)
                        .depth(2L)
                        .name("f2")
                        .parent(folder)
                        .build();

        memberRepository.save(member);
        folderRepository.save(folder);
        folderRepository.save(folder2);
    }

    private Card makeCard() {
        return Card.builder()
                   .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                   .title("카드 조회")
                   .content("카드 조회 issue")
                   .isPublic(true)
                   .folder(folder)
                   .build();
    }

    @Test
    @DisplayName("카드 추가 성공")
    public void saveCardSuccess() {
        // given
        final Card card = makeCard();

        // when
        final Card result = cardRepository.save(card);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
        assertThat(result.getTitle()).isEqualTo("카드 조회");
        assertThat(result.getContent()).isEqualTo("카드 조회 issue");
        assertThat(result.getIsPublic()).isEqualTo(true);
        assertThat(result.getFolder()
                         .getName()).isEqualTo("f1");
        assertThat(result.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("지정된 카드 조회 성공")
    public void findCardByIdSuccess() {
        // given
        final Card card = makeCard();
        final Card savedCard = cardRepository.save(card);

        // when
        cardRepository.findById(savedCard.getId())
                      // then
                      .ifPresent(selectCard -> {
                          assertThat(selectCard.getLink()).isEqualTo(savedCard.getLink());
                          assertThat(selectCard.getTitle()).isEqualTo(savedCard.getTitle());
                          assertThat(selectCard.getContent()).isEqualTo(savedCard.getContent());
                          assertThat(selectCard.getIsPublic()).isEqualTo(savedCard.getIsPublic());
                          assertThat(selectCard.getFolder()).isEqualTo(savedCard.getFolder());
                          assertThat(selectCard.getIsDeleted()).isEqualTo(false);
                      });
    }

    @Test
    @DisplayName("카드 수정 성공")
    public void updateCardSuccess() {
        // given
        final Card card = makeCard();
        final Card savedCard = cardRepository.save(card);

        // when
        cardRepository.findById(savedCard.getId())
                      .ifPresent(selectCard -> {
                          selectCard.updateLink("https://github.com/DHKH-null29/linky_way_back/issues/121");
                          selectCard.updateTitle("카드 조회2");
                          selectCard.updateContent("카드 조회 issue2");
                          selectCard.updateIsPublic(false);
                          selectCard.updateFolder(folder2);
                      });
        // then
        cardRepository.findById(savedCard.getId())
                      .ifPresent(result -> {
                          assertThat(result.getId()).isEqualTo(savedCard.getId());
                          assertThat(result.getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/121");
                          assertThat(result.getTitle()).isEqualTo("카드 조회2");
                          assertThat(result.getContent()).isEqualTo("카드 조회 issue2");
                          assertThat(result.getIsPublic()).isEqualTo(false);
                          assertThat(result.getFolder()).isEqualTo(folder2);
                          assertThat(result.getIsDeleted()).isEqualTo(false);
                      });
    }

    @ParameterizedTest
    @CsvSource(value = { "1,spring", "1,명륜진사갈비", "1,고기" })
    @DisplayName("findAllCardByKeyword 응답 테스트")
    void findAllCardByKeywordResponseTest(Long memberId, String keyword) {
        List<Card> cardList = cardRepository.findAllCardByKeyword(keyword, memberId);
        cardList.forEach(card -> {
            logger.info("id: {}, title: {}, link: {}, content: {}", card.getId(), card.getTitle(), card.getLink(),
                        card.getContent());
            assertThat(card.getTitle() + card.getContent()).contains(keyword);

        });
    }

    @Test
    @DisplayName("deleteAllCardTagInTagSet 테스트")
    void deleteAllCardTagInIds() {
        cardTagRepository.deleteAllCardTagInIds(Arrays.asList(1L, 3L, 5L));
        List<CardTag> cardTagList = cardTagRepository.findAll();
        cardTagList.forEach(cardTag -> {
            logger.info("{}", cardTag.getId());
        });
    }
    
    @Test
    @DisplayName("deleteAllCardTagInTagSet ids가 비어있는 경우 테스트")
    void deleteAllCardTagInIds_WhenIdsIsNotNull() {
        cardTagRepository.deleteAllCardTagInIds(new ArrayList<>());
        
    }

    @Test
    @DisplayName("findAllCardTagIdInTagSet 테스트")
    void findAllCardTagIdInTagSetTest() {
        Set<Long> s = new HashSet<>(Arrays.asList(1L, 2L));
        List<Long> list = cardTagRepository.findAllCardTagIdInTagSet(s);
        list.forEach(id -> {
            logger.info("{}", id);
        });

    }
    
    @Nested
    @DisplayName("findAllIdToDeletedCard paging 테스트")
    class findAllIdToDeletedCardPagingTest {
        @Test
        @DisplayName("findAllIdToDeletedCard page 테스트")
        void findAllIdToDeletedCardPageTest() {
        
            Slice<Long> idSlice = cardRepository.findAllIdToDeletedCardUsingPage(LocalDateTime.now().minusDays(7), PageRequest.of(0, 200));
            List<Long> ids = idSlice.getContent();
            assertThat(ids.size()).isEqualTo(1);
            Card c = cardRepository.findById(ids.get(0)).orElse(null);
            logger.info("{}",c.getModifiedBy());
            assertThat(c.getIsDeleted()).isEqualTo(true);
            
        }
        
        @Test
        void findAllIdToDeletedCardCursorPagingTest() {
            Slice<Long> idSlice = cardRepository.findAllIdToDeletedCardUsingCursorPage(LocalDateTime.now().minusDays(7),7L, PageRequest.of(0, 200));
            List<Long> ids = idSlice.getContent();
            assertThat(ids.size()).isEqualTo(1);
            Card c = cardRepository.findById(ids.get(0)).orElse(null);
            logger.info("{}",c.getModifiedBy());
            assertThat(c.getIsDeleted()).isEqualTo(true);
        }
    }
    
    @Nested
    @DisplayName("findAll paging 테스트")
    class findAllPageTest {
        
        @Test
        void findAllUsingPageTest() {
            Slice<Card> allUsingPage = cardRepository.findAllUsingPage(PageRequest.of(0, 100));
            assertThat(allUsingPage.getContent().size()).isEqualTo(6);
        }
    
        @Test
        void findAllUsingCursorPageTest() {
            Slice<Card> allUsingPage = cardRepository.findAllUsingCursorPage(7L, PageRequest.of(0, 100));
            assertThat(allUsingPage.getContent().size()).isEqualTo(6);
        }
    }
    
    @Test
    @DisplayName("findAllInIdsAndMemberId 테스트")
    void findAllInIdsAndMemberIdTest() {
        final Long INVALID_MEMBER_ID = 100L;
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 3L, INVALID_MEMBER_ID));
        List<Card> result = cardRepository.findAllInIdsAndMemberId(ids, 1L);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).extracting("id").isEqualTo(1L);
        assertThat(result.get(1)).extracting("id").isEqualTo(2L);
        assertThat(result.get(2)).extracting("id").isEqualTo(3L);
    }
    
    @Test
    @DisplayName("findAllByIsDeletedAndMemberId 테스트")
    void findAllByIsDeletedAndMemberIdTest() {
        List<Card> cardList = cardRepository.findAll();
        cardList.get(0).updateIsDeleted(true);
        cardList.get(1).updateIsDeleted(true);
        em.flush();
        
        List<Card> result = cardRepository.findAllByIsDeletedAndMemberIdUsingPage(true, 1L, PageRequest.of(0, 2));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).extracting("id").isEqualTo(6L);
        
    }
    
    @Test
    @DisplayName("findAllByIsDeletedAndMemberId CursorPaging 테스트")
    void findAllByIsDeletedAndMemberIdCursorPagingTest() {
        List<Card> cardList = cardRepository.findAll();
        cardList.get(0).updateIsDeleted(true);
        cardList.get(1).updateIsDeleted(true);
        em.flush();
    
        List<Card> result = cardRepository.findAllByIsDeletedAndMemberIdUsingCursorPage(true, 6L,1L, PageRequest.of(0, 2));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).extracting("id").isEqualTo(2L);

    }
    
    @Test
    @DisplayName("폴더 id로 카드 조회 테스트")
    void findAllInFolderIdsTest() {
        List<Card> cardList = cardRepository.findAllInFolderIds(Arrays.asList(1L, 2L, 3L));
        assertThat(cardList.stream().map(Card::getId).collect(Collectors.toList())).contains(1L, 2L, 6L);
    }
    
    @Test
    @DisplayName("해당 카드가 회원의 카드인지 검증")
    void findByCardIdAndMemberIdTest() {
        final long MEMBER_ID = 1L;
        final long CARD_ID = 1L;
        
        final long ANOTHER_MEMBER_ID = 3L;
    
        Card card = cardRepository.findByCardIdAndMemberId(CARD_ID, MEMBER_ID).orElse(null);
        assertThat(card).isNotNull();
        
        Card card2 = cardRepository.findByCardIdAndMemberId(CARD_ID, ANOTHER_MEMBER_ID).orElse(null);
        assertThat(card2).isNull();
    }
}
