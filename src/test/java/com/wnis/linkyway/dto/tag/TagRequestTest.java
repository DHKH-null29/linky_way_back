package com.wnis.linkyway.dto.tag;




import com.wnis.linkyway.validation.ValidationGroup;
import com.wnis.linkyway.validation.ValidationSequence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static com.wnis.linkyway.validation.ValidationGroup.*;
import static org.assertj.core.api.Assertions.assertThat;


class TagRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(TagRequest.class);

    private static Validator validator;
    private static ValidatorFactory factory;
    @BeforeAll
    public static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    @Nested
    class ValidationTest {
        @ParameterizedTest
        @CsvSource(value = {
                "'', '', 2",
                "111111121211111111111, hello, 0",
                "12, true, 0"
        }, delimiter = ',')
        void notBlankTest(String tagName, String shareable, int result) {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName(tagName)
                    .shareable(shareable)
                    .build();

            Set<ConstraintViolation<TagRequest>> constraintViolations = validator.validate(tagRequest, NotBlankGroup.class);
            int errorSize = constraintViolations.size();
            constraintViolations.forEach((error)->
                    logger.debug(error.getMessage()));
            assertThat(errorSize).isEqualTo(result);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "'', '', 1",
                "111111121211111111111, hello, 1",
                "12, true, 0"
        }, delimiter = ',')
        void patternTest(String tagName, String shareable, int result) {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName(tagName)
                    .shareable(shareable)
                    .build();

            Set<ConstraintViolation<TagRequest>> constraintViolations = validator.validate(tagRequest, PatternCheckGroup.class);
            int errorSize = constraintViolations.size();
            constraintViolations.forEach((error)->
                    logger.debug(error.getMessage()));
            assertThat(errorSize).isEqualTo(result);
        }
    }


}