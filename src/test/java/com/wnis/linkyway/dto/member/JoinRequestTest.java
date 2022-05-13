package com.wnis.linkyway.dto.member;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class JoinRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(JoinRequestTest.class);

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

    @ParameterizedTest
    @CsvSource(value = {
            "ksh@naver.com, 12345, hello, 1",
            "kkk@asfa121, aA1!*asda111, hel(lo, 2",
            "'1', '', '', 3"
    }, delimiter = ',')
    void validationTest(String email, String password, String nickname, int result) {
        JoinRequest joinRequest = JoinRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Set<ConstraintViolation<JoinRequest>> constraintViolations = validator.validate(joinRequest);
        int errorSize = constraintViolations.size();
        constraintViolations.forEach((error)->
                logger.debug(error.getMessage()));
        assertThat(errorSize).isEqualTo(result);
    }
}