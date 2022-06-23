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
import org.springframework.data.domain.PageRequest;
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
    
        List<PackageResponse> spring = packageService.findAllPackageByTagName("spring", PageRequest.of(0, 1));
        assertThat(spring.size()).isEqualTo(1);
        spring.forEach(packageResponse -> {
            assertThat(packageResponse).extracting("tagName").isEqualTo("spring");
        });
        
        List<PackageResponse> food = packageService.findAllPackageByTagName("food", PageRequest.of(0, 2));
        logger.info("{}", food.get(0).getNumberOfCard());
        assertThat(food.get(0).getNumberOfCard()).isEqualTo(1);
        food.forEach(packageResponse -> {
            assertThat(packageResponse).extracting("tagName").isEqualTo("food");
        });
    }
}
