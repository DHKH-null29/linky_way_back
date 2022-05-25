package com.wnis.linkyway.dto.card;

import com.wnis.linkyway.validation.ValidationGroup;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.*;



class CardRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(CardRequestTest.class);

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
                "www.google.co.kr, '', '', 1, 1",
                "'', '', false, '', 2",
                "'', '', '', '', 3"
        }, delimiter = ',')
        void notBlankTest(String link, String title, Boolean shareable, Long folderId, int result) {
            CardRequest cardRequest = CardRequest.builder()
                    .link(link)
                    .title(title)
                    .shareable(shareable)
                    .folderId(folderId)
                    .build();

            Set<ConstraintViolation<CardRequest>>constraintViolations = validator.validate(cardRequest, NotBlankGroup.class);
            int errorSize = constraintViolations.size();
            for (ConstraintViolation<CardRequest> constraintViolation : constraintViolations) {
                logger.debug("violdation message: {}", constraintViolation.getMessage());
            }
            assertThat(errorSize).isEqualTo(result);

        }

        @ParameterizedTest
        @CsvSource(value = {
                "www.google.co.kr, '', '', 1, 1",
                "'', '', false, '', 0",
                "'', '', 'true', '', 0",
                "'', '', 'trye1', '', 1"
        }, delimiter = ',')
        void patternTest(String link, String title, Boolean shareable, Long folderId, int result) {
            CardRequest cardRequest = CardRequest.builder()
                    .link(link)
                    .title(title)
                    .shareable(shareable)
                    .folderId(folderId)
                    .build();

            Set<ConstraintViolation<CardRequest>>constraintViolations = validator.validate(cardRequest, PatternCheckGroup.class);
            int errorSize = constraintViolations.size();
            for (ConstraintViolation<CardRequest> constraintViolation : constraintViolations) {
                logger.debug("violdation message: {}", constraintViolation.getMessage());
            }
            assertThat(errorSize).isEqualTo(result);

        }
    }




}