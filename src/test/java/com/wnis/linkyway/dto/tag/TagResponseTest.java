package com.wnis.linkyway.dto.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class TagResponseTest {

    @Autowired
    JacksonTester<TagResponse> jacksonTester;

    @Test
    void serializedTest() throws IOException {
        TagResponse tagResponse = TagResponse.builder()
                                             .tagName("hello")
                                             .tagId(1L)
                                             .isPublic(true)
                                             .build();

        assertThat(jacksonTester.write(tagResponse)).hasJsonPathValue("@.tagName");
        assertThat(jacksonTester.write(tagResponse)).hasJsonPathValue("@.tagId");
        assertThat(jacksonTester.write(tagResponse)).hasJsonPathValue("@.isPublic");
    }

}