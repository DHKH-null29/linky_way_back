package com.wnis.linkyway.service.card;

import static org.assertj.core.api.Assertions.assertThat;

import com.wnis.linkyway.config.QueryDslConfiguration;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.repository.card.CardRepositoryCustom;
import com.wnis.linkyway.repository.cardtag.CardTagRepository;
import com.wnis.linkyway.repository.cardtag.CardTagRepositoryCustom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/card-test.sql")
@Import({CardServiceImpl.class, QueryDslConfiguration.class, CardTagRepositoryCustom.class,
    CardRepositoryCustom.class})
public class CardServiceLogicTest {

    @Autowired
    EntityManager em;
    @Autowired
    CardService cardService;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardTagRepository cardTagRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("카드 deep search test")
    void cardTest() {
        Member member = em.find(Member.class, 1L);
        Folder folder2 = em.find(Folder.class, 2L);

        Folder newFolder1 = Folder.builder()
            .member(member)
            .parent(folder2)
            .name("hello")
            .depth(folder2.getDepth() + 1)
            .build();

        Folder newFolder2 = Folder.builder()
            .member(member)
            .parent(newFolder1)
            .name("heasdfaf")
            .depth(newFolder1.getDepth())
            .build();
        Card newCard1 = Card.builder()
            .folder(newFolder1)
            .title("h")
            .content("h")
            .link("https://www.google.com")
            .isPublic(false)
            .build();

        Card newCard2 = Card.builder()
            .folder(newFolder2)
            .title("c")
            .content("k")
            .link("https://www.naver.com")
            .isPublic(false)
            .build();

        em.persist(newFolder1);
        em.persist(newFolder2);
        em.persist(newCard1);
        em.persist(newCard2);
        em.flush();

        List<CardResponse> cardResponseList = cardService.findCardsByFolderId(null, 1L, 2L, true,
            PageRequest.of(0, 200));
        logger.info("{}", cardResponseList);
        assertThat(cardResponseList.size()).isEqualTo(5);
    }

    @Nested
    @DisplayName("카드 추가")
    class AddCardTest {

        @Test
        @DisplayName("응답 테스트")
        void addCardSuccessTest() {
            Set<Long> tagIdSet = new HashSet<>(Arrays.asList(1L, 2L, 3L));
            CardRequest cardRequest = CardRequest.builder()
                .link("www.google.com")
                .title("구글 주소")
                .content("hello world")
                .isPublic(false)
                .tagIdSet(tagIdSet)
                .folderId(1L)
                .build();
            int beforeSize = cardRepository.findAll().size();
            int beforeCardTagSize = cardTagRepository.findAll().size();
            cardService.addCard(1L, cardRequest);
            List<CardTag> cardTagList = cardTagRepository.findAll();
            cardTagList.forEach(cardTag -> {
                logger.info("CardTag ID: {}, Card ID: {}, Card Title: {},TagID: {} Tag Name: {}",
                    cardTag.getId(), cardTag.getCard().getId(), cardTag.getCard().getTitle(),
                    cardTag.getTag().getId(), cardTag.getTag().getName());
                Long id = cardTag.getId();
                if (id > 6 && id < 10) {
                    assertThat(cardTag.getCard().getId()).isEqualTo(7);
                }
            });
            assertThat(cardRepository.findAll().size()).isEqualTo(beforeSize + 1);
            assertThat(cardTagRepository.findAll().size()).isEqualTo(beforeCardTagSize + 3);
        }


        @Test
        @DisplayName("응답 테스트 태그가 비어있는 경우")
        void addCardSuccessWhenNoTagTest() {
            CardRequest cardRequest = CardRequest.builder()
                .link("www.google.com")
                .isPublic(false)
                .folderId(1L)
                .title("world")
                .content("hello")
                .tagIdSet(new HashSet<>(Arrays.asList()))
                .build();
            int beforeSize = cardRepository.findAll().size();
            int beforeCardTagSize = cardTagRepository.findAll().size();
            cardService.addCard(1L, cardRequest);
            List<CardTag> cardTagList = cardTagRepository.findAll();
            cardTagList.forEach(cardTag -> {
                logger.info("CardTag ID: {}, Card ID: {}, Card Title: {},TagID: {} Tag Name: {}",
                    cardTag.getId(), cardTag.getCard().getId(), cardTag.getCard().getTitle(),
                    cardTag.getTag().getId(), cardTag.getTag().getName());
                Long id = cardTag.getId();
                if (id > 6 && id < 10) {
                    assertThat(cardTag.getCard().getId()).isEqualTo(7);
                }
            });
            assertThat(cardRepository.findAll().size()).isEqualTo(beforeSize + 1);
            assertThat(cardTagRepository.findAll().size()).isEqualTo(
                beforeCardTagSize); // 카드 태그는 변화 없음
        }

    }

    @Nested
    @DisplayName("카드 조회")
    class findCardByCardIdTest {

        @Test
        @DisplayName("카드 조회 성공 테스트")
        void findCardByCardIdSuccessTest() {
            CardResponse cardResponse = cardService.findCardByCardId(3L, 1L);
            assertThat(cardResponse.getCardId()).isNotNull();
            assertThat(cardResponse.getLink()).isNotNull();
            assertThat(cardResponse.getTitle()).isNotNull();
            assertThat(cardResponse.getContent()).isNotNull();
            assertThat(cardResponse.getIsPublic()).isNotNull();
            assertThat(cardResponse.getTags()).isNotNull();
        }
    }

    @Nested
    @DisplayName("카드 수정")
    class UpdateCardTest {

        @Test
        @DisplayName("카드 수정 성공 테스트")
        void updateCardSuccessTest() {
            // 카드 2의 태그는 1
            // 수정사항 태그 2,3
            // 1. 기존 카드, 태그 (2, 1) 관계는 소멸해야함
            // 2. (카드, 태그) (2, 2), (2, 3) 추가되야함
            // 3. 카드 내용물도 업데이트 되야함
            Long memberId = 1L;
            Long cardId = 2L;
            Set<Long> tagIdSet = new HashSet<>(Arrays.asList(2L, 3L));
            CardRequest cardRequest = CardRequest.builder()
                .link("www.google.com")
                .isPublic(false)
                .title("hello")
                .content("world")
                .tagIdSet(tagIdSet)
                .folderId(1L)
                .build();
            Card beforeCard = cardRepository.findById(cardId).orElse(null);
            em.detach(beforeCard);
            cardService.updateCard(memberId, cardId, cardRequest);

            Card afterCard = cardRepository.findById(cardId).orElse(null);
            assertThat(afterCard.getId()).isEqualTo(beforeCard.getId());
            assertThat(afterCard.getLink()).isNotEqualTo(beforeCard.getLink());
            assertThat(afterCard.getFolder().getId()).isNotEqualTo(beforeCard.getFolder().getId());

            List<CardTag> cardTagList = cardTagRepository.findAll();
            cardTagList.forEach(cardTag -> {
                logger.info("card ID: {}, tag ID: {}",
                    cardTag.getCard().getId(), cardTag.getTag().getId());
                if (cardTag.getCard().getId() == 2) {
                    assertThat(cardTag.getTag().getId()).isIn(2L, 3L);
                }
            });

        }

    }

}
