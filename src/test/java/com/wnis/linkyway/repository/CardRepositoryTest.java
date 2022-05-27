package com.wnis.linkyway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.wnis.linkyway.entity.Card;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        final Card card = Card.builder()
                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                              .title("카드 조회")
                              .content("카드 조회 issue")
                              .shareable(true)
                              .folder(null)
                              .build();

        // when
        final Card result = cardRepository.save(card);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getLink()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("카드 조회");
        assertThat(result.getContent()).isEqualTo("카드 조회 issue");
        assertThat(result.getShareable()).isEqualTo(true);
        assertThat(result.getFolder()).isNull();
    }
}
