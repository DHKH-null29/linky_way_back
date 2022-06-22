package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

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
                       .name("f")
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
    @DisplayName("지정된 카드 조회 성공")
    public void findCardByIdSuccess() {
        // given
        final Card card = makeCard();
        final Card savedCard = cardRepository.save(card);

        // when
        cardRepository.findById(savedCard.getId())
                      // then
                      .ifPresent(selectCard -> { // 카드 존재 시 출력
                          System.out.println("card:" + selectCard.getId() + selectCard.getTitle());
                      });

        assertThat(savedCard.getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
        assertThat(savedCard.getTitle()).isEqualTo("카드 조회");
        assertThat(savedCard.getContent()).isEqualTo("카드 조회 issue");
        assertThat(savedCard.getIsPublic()).isEqualTo(true);
        assertThat(savedCard.getFolder()).isEqualTo(folder);
        assertThat(savedCard.getIsDeleted()).isEqualTo(false);
    }

    
//    @Test
//    @DisplayName("카드(북마크) 추가 성공")
//    public void addCardSuccess() {
//        // given
//        final Card card = Card.builder()
//                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
//                              .title("카드 조회")
//                              .content("카드 조회 issue")
//                              .isPublic(true)
//                              .folder(null)
//                              .build();
//
//        // when
//        final Card result = cardRepository.save(card);
//
//        // then
//        assertThat(result.getId()).isNotNull();
//        assertThat(result.getLink()).isNotNull();
//        assertThat(result.getTitle()).isEqualTo("카드 조회");
//        assertThat(result.getContent()).isEqualTo("카드 조회 issue");
//        assertThat(result.getIsPublic()).isEqualTo(true);
//        assertThat(result.getFolder()).isNull();
//    }
//
//    @Test
//    @DisplayName("지정된 카드 조회 성공")
//    public void findCardByIdSuccess() {
//        // given
//        final Card card = Card.builder()
//                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
//                              .title("카드 조회")
//                              .content("카드 조회 issue")
//                              .isPublic(true)
//                              .folder(null)
//                              .build();
//        final Card savedCard = cardRepository.save(card);
//
//        // when
//        Card findCard = cardRepository.findById(savedCard.getId())
//                                      .orElseThrow(() -> new IllegalArgumentException("Wrong CardId"));
//
//        // then
//        cardRepository.findById(findCard.getId())
//                      .ifPresent(selectCard -> { // 카드
//                          // 존재
//                          // 시
//                          // 출력
//                          System.out.println("card:" + selectCard.getId() + selectCard.getTitle());
//                      });
//
//        assertThat(findCard.getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
//        assertThat(findCard.getTitle()).isEqualTo("카드 조회");
//        assertThat(findCard.getContent()).isEqualTo("카드 조회 issue");
//        assertThat(findCard.getIsPublic()).isEqualTo(true);
//        assertThat(findCard.getFolder()).isNull();
//    }
//
//    @Test
//    @DisplayName("카드(북마크) 수정 성공")
//    public void updateCardSuccess() {
//        // given
//        final Card card = Card.builder()
//                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
//                              .title("카드 조회")
//                              .content("카드 조회 issue")
//                              .isPublic(true)
//                              .folder(null)
//                              .build();
//        final Card savedCard = cardRepository.save(card);
//
//        // when
//        Optional<Card> resultCard = cardRepository.findById(savedCard.getId());
//        resultCard.ifPresent(selectCard -> {
//            selectCard.updateLink("https://github.com/DHKH-null29/linky_way_back/issues/12");
//            selectCard.updateTitle("카드 조회2");
//            selectCard.updateContent("카드 조회 issue2");
//            selectCard.updateIsPublic(false);
//            cardRepository.flush();
//        });
//
//        // then
//        Optional<Card> result = cardRepository.findById(savedCard.getId());
//        assertThat(result.get()
//                         .getId()).isEqualTo(savedCard.getId());
//        assertThat(result.get()
//                         .getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
//        assertThat(result.get()
//                         .getTitle()).isEqualTo("카드 조회2");
//        assertThat(result.get()
//                         .getContent()).isEqualTo("카드 조회 issue2");
//        assertThat(result.get()
//                         .getIsPublic()).isEqualTo(false);
//        assertThat(result.get()
//                         .getFolder()).isNull();
//    }
    
    
    @ParameterizedTest
    @CsvSource(value = {
            "1,spring",
            "1,명륜진사갈비",
            "1,고기"
    })
    @DisplayName("findAllCardByKeyword 응답 테스트")
    void findAllCardByKeywordResponseTest(Long memberId, String keyword) {
        List<Card> cardList = cardRepository.findAllCardByKeyword(keyword, memberId);
        cardList.forEach(card -> {
            logger.info("id: {}, title: {}, link: {}, content: {}",
                        card.getId(), card.getTitle(), card.getLink(), card.getContent() );
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
    @DisplayName("findAllCardTagIdInTagSet 테스트")
    void findAllCardTagIdInTagSetTest() {
        Set<Long> s = new HashSet<>(Arrays.asList(1L, 2L));
        List<Long> list = cardTagRepository.findAllCardTagIdInTagSet(s);
        list.forEach(id-> {
            logger.info("{}", id);
        });
    
    }
    
    @Test
    @DisplayName("findAllIdToDeletedCard 테스트")
    void findAllIdToDeletedCardTest() {
        
        List<Long> ids = cardRepository.findAllIdToDeletedCard(LocalDateTime.now().minusDays(7));
        assertThat(ids.size()).isEqualTo(1);
        Card c = cardRepository.findById(ids.get(0)).orElse(null);
        logger.info("{}",c.getModifiedBy());
        assertThat(c.getIsDeleted()).isEqualTo(true);
    }
    
    @Test
    void test() {
        final Long INVALID_MEMBER_ID = 100L;
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 3L, INVALID_MEMBER_ID));
        List<Card> result = cardRepository.findAllInIdsAndMemberId(ids, 1L);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).extracting("id").isEqualTo(1L);
        assertThat(result.get(1)).extracting("id").isEqualTo(2L);
        assertThat(result.get(2)).extracting("id").isEqualTo(3L);
    }
}
