package com.wnis.linkyway.repository.card;

import static org.assertj.core.api.Assertions.assertThat;

import com.wnis.linkyway.config.QueryDslConfiguration;
import com.wnis.linkyway.dto.card.CardDto;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({QueryDslConfiguration.class, CardRepositoryCustom.class})
@Sql("/sqltest/card-test.sql")
class CardRepositoryCustomTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    EntityManager em;

    @Autowired
    CardRepositoryCustom cardRepositoryCustom;


    @Nested
    class findAllCardContainKeywordTest {

        @Test
        void checkCursorPaging() {
            // first search(lastIndex 가 null인 경우 -> offset으로 동작해야함
            List<CardDto> list = cardRepositoryCustom.findAllCardContainKeyword(null,
                "", 1L, PageRequest.of(0, 2));
            assertThat(list.size()).isEqualTo(2);
            assertThat(list).extracting("id")
                .contains(5L, Index.atIndex(0))
                .contains(4L, Index.atIndex(1));

            // second search(lastIndex가 존재하는 경우 -> 커서페이징 탐색
            List<CardDto> cursorList = cardRepositoryCustom.findAllCardContainKeyword(5L, "", 1L,
                PageRequest.of(0, 3));
            assertThat(cursorList.size()).isEqualTo(3);
            assertThat(cursorList).extracting("id")
                .contains(4L, Index.atIndex(0))
                .contains(3L, Index.atIndex(1))
                .contains(2L, Index.atIndex(2));
        }

        @Test
        void checkKeyword() {
            List<CardDto> list = cardRepositoryCustom.findAllCardContainKeyword(null, "숯불", 1L,
                PageRequest.of(0, 5));
            list.forEach(cardDto -> {
                boolean hasWord = cardDto.getContent().contains("숯불") ||
                    cardDto.getTitle().contains("숯불");

                assertThat(hasWord).isEqualTo(true);
            });
        }
    }

    @Nested
    class findAllCardByTadIdTest {

        @Test
        void checkCursorPaging() {
            // first search(lastIndex 가 null인 경우 -> offset으로 동작해야함
            List<CardDto> cardDtoList = cardRepositoryCustom.findAllCardByTadId(null, 2L,
                PageRequest.of(0, 3));
            assertThat(cardDtoList.size()).isEqualTo(3);
            assertThat(cardDtoList).extracting("id").contains(5L, Index.atIndex(0));

            // second search(lastIndex가 존재하는 경우 -> 커서페이징 탐색
            List<CardDto> cardDtoList2 = cardRepositoryCustom.findAllCardByTadId(6L, 2L,
                PageRequest.of(0, 3));

//            System.out.println(cardDtoList2);
            assertThat(cardDtoList2.size()).isEqualTo(3);
            assertThat(cardDtoList2).extracting("id").contains(5L, Index.atIndex(0));
        }
    }


    @Nested
    class findAllCardByMemberId {

        @Test
        void checkCursorPaging() {
            // first search(lastIndex 가 null인 경우 -> offset으로 동작해야함
            List<CardDto> cardDtoList = cardRepositoryCustom.findAllCardByMemberId(null, 1L,
                PageRequest.of(0, 5));
            logger.info("{}", cardDtoList);
            assertThat(cardDtoList.size()).isEqualTo(5);
        }
    }

}