package com.wnis.linkyway.exception.error;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class ErrorResponseTest {
    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseTest.class);

    @Autowired
    JacksonTester<ErrorResponse> jacksonTester;

    @Test
    void serializedTest() throws IOException {
        ErrorResponse errorResponse =
                ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,"매우매우 나쁜 요청", "/login");

        JsonContent<ErrorResponse> json = jacksonTester.write(errorResponse);
        logger.info(String.valueOf(json));
        assertThat(json).hasJsonPathValue("@.code");
        assertThat(json).hasJsonPathValue("@.message");
        assertThat(json).hasJsonPathValue("@.path");
        assertThat(json).hasEmptyJsonPathValue("@.errors");
    }
}