package com.wnis.linkyway.dto.member;




import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.validation.ValidationGroup;
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

public class LoginRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginRequest.class);

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
                "adg@naa, asdfa, 0",
                "'', 'a', 1"
        })
        void notBlankTest(String email, String password, int result) {
            LoginRequest loginRequest = LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();

            Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(loginRequest, NotBlankGroup.class);
            constraintViolations.forEach((error)->
                    logger.debug(error.getMessage()));
            assertThat(result).isEqualTo(result);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "'', '', 2",
                "adg@naa, asdfa, 2",
                "'asd12@co.kr', 'aA1!aa121', 0"
        })
        void PatternTest(String email, String password, int result) {
            LoginRequest loginRequest = LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();

            Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(loginRequest, PatternCheckGroup.class);
            int errorSize = constraintViolations.size();
            constraintViolations.forEach((error)->
                    logger.debug(error.getMessage()));
            assertThat(errorSize).isEqualTo(result);
        }
    }

}
