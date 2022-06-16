package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.repository.CardTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PackageService.class})
@Sql("/sqltest/card-test.sql")
public class PackageServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired CardTagRepository cardTagRepository;
    @Autowired PackageService packageService;
    
    @Test
    @DisplayName("findAllPackageByTagNameTest")
    void findAllPackageByTagNameTest() {
    
        List<PackageResponse> java = packageService.findAllPackageByTagName("java");
        java.forEach(packageResponse -> {
            assertThat(packageResponse).extracting("tagName").isEqualTo("java");
        });
    
        // 조회시는 대소문자 구분 없이 모두 조회
        List<PackageResponse> food = packageService.findAllPackageByTagName("FoOd");
        logger.info("{}", food.get(0).getNumberOfCard());
        assertThat(food.get(0).getNumberOfCard()).isEqualTo(4);
        food.forEach(packageResponse -> {
            assertThat(packageResponse).extracting("tagName").isEqualTo("food");
        });
    }
}
