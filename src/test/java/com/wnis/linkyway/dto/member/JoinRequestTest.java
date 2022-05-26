package com.wnis.linkyway.dto.member;


import com.wnis.linkyway.validation.ValidationGroup;
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
    
    @Test
    @DisplayName("NotBlank 테스트")
    void NotBlankTest() {
        JoinRequest joinRequest = JoinRequest.builder().build();
        
        Set<ConstraintViolation<JoinRequest>> constraintViolations = validator
                .validate(joinRequest, ValidationGroup.NotBlankGroup.class);
        
        assertThat(constraintViolations.size()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("패턴 테스트")
    void patternTest() {
        JoinRequest joinRequest = JoinRequest.builder()
                .email("adfdf@ac").password("123").nickname("a@").build();
        
        Set<ConstraintViolation<JoinRequest>> constraintViolations = validator
                .validate(joinRequest, ValidationGroup.PatternCheckGroup.class);
        
        assertThat(constraintViolations.size()).isEqualTo(3);
    }
}