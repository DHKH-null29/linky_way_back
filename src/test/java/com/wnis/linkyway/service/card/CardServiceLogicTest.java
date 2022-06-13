package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/card-test.sql")
@Import(CardServiceImpl.class)
public class CardServiceLogicTest {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired EntityManager em;
    @Autowired CardService cardService;
    @Autowired CardRepository cardRepository;
    @Autowired CardTagRepository cardTagRepository;
    
    
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
        @DisplayName("응답 테스트 title이 없는 경우")
        void addCardSuccessWhenNoTitleTest() {
            Set<Long> tagIdSet = new HashSet<>(Arrays.asList(1L, 2L, 3L));
            CardRequest cardRequest = CardRequest.builder()
                                                 .link("www.google.com")
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
        @DisplayName("응답 테스트 태그가 없는 경우")
        void addCardSuccessWhenNoTagTest() {
            CardRequest cardRequest = CardRequest.builder()
                                                 .link("www.google.com")
                                                 .isPublic(false)
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
            assertThat(cardTagRepository.findAll().size()).isEqualTo(beforeCardTagSize); // 카드 태그는 변화 없음
        }
       
    }
    
    @Nested
    @DisplayName("카드 조회")
    class findCardByCardIdTest {
        
        @Test
        @DisplayName("카드 조회 성공 테스트")
        void findCardByCardIdSuccessTest() {
            CardResponse cardResponse = cardService.findCardByCardId(3L);
            assertThat(cardResponse.getCardId()).isNotNull();
            assertThat(cardResponse.getLink()).isNotNull();
            assertThat(cardResponse.getTitle()).isNotNull();
            assertThat(cardResponse.getContent()).isNotNull();
            assertThat(cardResponse.getIsPublic()).isNotNull();
            assertThat(cardResponse.getIsDeleted()).isNotNull();
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