package com.wnis.linkyway.dto.member;

import com.wnis.linkyway.validation.ValidationGroup;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(PasswordRequestTest.class);

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

    @Test
    @DisplayName("Not Blank 테스트")
    void notBlankTest() {
        PasswordRequest passwordRequest = PasswordRequest.builder()
                                                         .password("")
                                                         .build();

        Set<ConstraintViolation<PasswordRequest>> constraintViolations = validator.validate(passwordRequest,
                                                                                            ValidationGroup.NotBlankGroup.class);

        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("password를 입력해주세요");
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }

    @Test
    @DisplayName("pattern 테스트")
    void patternTest() {
        PasswordRequest passwordRequest = PasswordRequest.builder()
                                                         .password("asa")
                                                         .build();

        Set<ConstraintViolation<PasswordRequest>> constraintViolations = validator.validate(passwordRequest,
                                                                                            ValidationGroup.PatternCheckGroup.class);

        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("최소 대/소 문자 하나, 숫자 하나, 특수문자를 4 ~ 16 글자로 입력해주세요");
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }
}