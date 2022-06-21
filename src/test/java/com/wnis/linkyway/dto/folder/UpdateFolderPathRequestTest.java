package com.wnis.linkyway.dto.folder;

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

class UpdateFolderPathRequestTest {
    private static Validator validator;
    private static ValidatorFactory factory;
    private final Logger logger = LoggerFactory.getLogger(UpdateFolderPathRequestTest.class);

    @BeforeAll
    static void setup() {
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
        UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                        .build();

        Set<ConstraintViolation<UpdateFolderPathRequest>> constraintViolations = validator.validate(updateFolderPathRequest,
                                                                                                 ValidationGroup.NotBlankGroup.class);

        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }

    @Test
    @DisplayName("positive 테스트")
    void positiveTest() {
        UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                        .targetFolderId(-1L)
                                                                        .build();

        Set<ConstraintViolation<UpdateFolderPathRequest>> constraintViolations = validator.validate(updateFolderPathRequest);

        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }

    @Test
    @DisplayName("통과 테스트")
    void successTest() {
        UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                        .targetFolderId(1L)
                                                                        .build();

        Set<ConstraintViolation<UpdateFolderPathRequest>> constraintViolations = validator.validate(updateFolderPathRequest);
        assertThat(constraintViolations).isEmpty();
    }
}