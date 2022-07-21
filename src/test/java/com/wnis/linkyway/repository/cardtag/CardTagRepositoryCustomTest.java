package com.wnis.linkyway.repository.cardtag;

import static org.assertj.core.api.Assertions.assertThat;

import com.wnis.linkyway.config.QueryDslConfiguration;
import com.wnis.linkyway.dto.cardtag.CardTagDto;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({QueryDslConfiguration.class, CardTagRepositoryCustom.class})
@Sql("/sqltest/card-test.sql")
class CardTagRepositoryCustomTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EntityManager em;

    @Autowired
    CardTagRepositoryCustom cardTagRepositoryCustom;

    @Test
    void findCardTagByCardIdTest() {
        List<CardTagDto> cardTagByCardId = cardTagRepositoryCustom.findCardTagByCardId(
            new HashSet<>(Arrays.asList(1L, 2L, 3L)));


        for (int i = 0; i < cardTagByCardId.size(); i++) {
            CardTagDto cardTagDto = cardTagByCardId.get(i);
            assertThat(cardTagDto.getCardId()).isEqualTo(i+1);
            logger.info("{}", cardTagDto);
        }

    }
}