package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.repository.card.CardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/card-test.sql")
@Import({TrashService.class})
public class TrashServiceTest {
    
    @Autowired
    TrashService trashService;
    
    @Autowired
    CardRepository cardRepository;
    
    @Autowired
    EntityManager em;
    
    private final long VALID_MEMBER_ID = 1L;
    private final long INVALID_MEMBER_ID = 100L;
    
    
    @Test
    @DisplayName("카드 복원")
    void shouldChangeCardIsDeletedPropertiesTest() {
        // 내부 속성이 제대로 변했는지 테스트
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        trashService.updateDeleteCardFalse(ids, VALID_MEMBER_ID);
        em.flush();
        em.clear();
        
        Card c1 = em.find(Card.class, 1L);
        Card c2 = em.find(Card.class, 2L);
        
        assertThat(c1.getIsDeleted()).isEqualTo(false);
        assertThat(c2.getIsDeleted()).isEqualTo(false);
    }
    
    @Test
    @DisplayName("완전한 카드 삭제")
    void shouldDeleteCardCompletelyInDB() {
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        List<Long> deletedIds = trashService.deleteCompletely(ids, 1L);
        List<Long> idList = cardRepository.findAll().stream().map(Card::getId).collect(Collectors.toList());
        assertThat(idList.size()).isEqualTo(3);
        assertThat(idList).doesNotContain(1L, 2L, 3L);
        em.flush();
        
    }
    
    @Test
    @DisplayName("삭제된 카드 조회")
    void shouldReturnDeletedCardFormatTest() {
        // 제대로 응답 되는지 테스트
        List<CardResponse> cardResponseList = trashService.findAllDeletedCard(VALID_MEMBER_ID, 7L, PageRequest.of(0, 2));
        
        assertThat(cardResponseList.size()).isEqualTo(1);
        assertThat(cardResponseList.get(0).getCardId()).isNotNull();
        assertThat(cardResponseList.get(0).getFolderId()).isNotNull();
        assertThat(cardResponseList.get(0).getIsPublic()).isNotNull();
        assertThat(cardResponseList.get(0).getTitle()).isNotNull();
        assertThat(cardResponseList.get(0).getLink()).isNotNull();
        assertThat(cardResponseList.get(0).getContent()).isNotNull();
        assertThat(cardResponseList.get(0).getTags()).isNotNull();
    }
}
