package com.wnis.linkyway.service;

import com.wnis.linkyway.entity.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/card-test.sql")
@Import({TrashService.class})
public class TrashServiceTest {
    
    @Autowired
    TrashService trashService;
    
    @Autowired
    EntityManager em;
    
    private final long VALID_MEMBER_ID = 1L;
    private final long INVALID_MEMBER_ID = 100L;
    
    
    @Test
    @DisplayName("카드 삭제 복원")
    void shouldChangeCardIsDeletedPropertiesTest() {
        // 내부 속성이 제대로 변했는지 테스트
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        trashService.updateDeleteCardTrueOrFalse(ids, VALID_MEMBER_ID, true);
        em.flush();
        em.clear();
        
        Card c1 = em.find(Card.class, 1L);
        Card c2 = em.find(Card.class, 2L);
        
        assertThat(c1.getIsDeleted()).isEqualTo(true);
        assertThat(c2.getIsDeleted()).isEqualTo(true);
    }
}
