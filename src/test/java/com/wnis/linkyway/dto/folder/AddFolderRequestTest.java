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


class AddFolderRequestTest {
    
    private static Validator validator;
    private static ValidatorFactory factory;
    private final Logger logger = LoggerFactory.getLogger(AddFolderRequestTest.class);
    
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
        AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                .build();
        
        Set<ConstraintViolation<AddFolderRequest>> constraintViolations = validator
                .validate(addFolderRequest, ValidationGroup.NotBlankGroup.class);
        
        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }
    
    @Test
    @DisplayName("Pattern 테스트")
    void patternTest() {
        AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                .parentFolderId(0L)
                .name("가@")
                .build();
        
        Set<ConstraintViolation<AddFolderRequest>> constraintViolations = validator
                .validate(addFolderRequest, ValidationGroup.PatternCheckGroup.class);
        
        constraintViolations.forEach((e) -> {
            logger.info(e.getMessage());
            assertThat(e).isInstanceOf(ConstraintViolationImpl.class);
        });
    }
    
    @Test
    @DisplayName("통과 테스트")
    void successTest() {
        AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                .parentFolderId(0L)
                .name("가")
                .build();
        
        Set<ConstraintViolation<AddFolderRequest>> constraintViolations = validator
                .validate(addFolderRequest, ValidationGroup.PatternCheckGroup.class);
        
        assertThat(constraintViolations).isEmpty();
    }
}