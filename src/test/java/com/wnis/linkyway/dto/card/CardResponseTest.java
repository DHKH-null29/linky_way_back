package com.wnis.linkyway.dto.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class CardResponseTest {
    private static final Logger logger = LoggerFactory.getLogger(CardResponseTest.class);
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JacksonTester<CardResponse> jacksonTester;

    @Nested
    @DisplayName("직렬화 테스트")
    class SerializedTest {
        @Test
        @DisplayName("JsonInclude.NON_EMPTY 테스트")
        void jacksonEmptyTest() throws JsonProcessingException {
            CardResponse cardResponse = CardResponse.builder()
                    .cardId(1L)
                    .title("hello")
                    .content("world")
                    .build();

            // null empty 값은 출력되면 안된다.
            // {"cardId":1,"title":"hello","content":"world","shareable":false}
            String serializedCardResponse = objectMapper.writeValueAsString(cardResponse);
            logger.info("json 직렬화 데이터 결과: " + serializedCardResponse);
            assertThat(serializedCardResponse).isEqualTo(
                    "{\"cardId\":1,\"title\":\"hello\",\"content\":\"world\",\"shareable\":false}"
            );
        }

        @Test
        @DisplayName("연관관계가 포함된 응답 테스트")
        void relatedTagTest() throws IOException {
            Tag tag1 = Tag.builder()
                    .name("cat")
                    .build();

            Tag tag2 = Tag.builder()
                    .name("dog")
                    .build();

            List<Tag> tags = new ArrayList<>(Arrays.asList(tag1, tag2));
            CardResponse cardResponse = CardResponse.builder()
                    .cardId(1L)
                    .title("hello")
                    .content("world")
                    .shareable(true)
                    .link("www.google.com")
                    .tags(tags)
                    .build();

            String serializedCardResponse = objectMapper.writeValueAsString(cardResponse);
            assertThat(jacksonTester.write(cardResponse)).hasJsonPathArrayValue("@.tags");
            logger.info(serializedCardResponse);
        }
    }



}