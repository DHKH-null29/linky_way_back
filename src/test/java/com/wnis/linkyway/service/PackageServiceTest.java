package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.entity.Card;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PackageService.class})
@Sql("/sqltest/card-test.sql")
public class PackageServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CardTagRepository cardTagRepository;
    @Autowired
    PackageService packageService;
    
    @Autowired
    EntityManager em;
    
    @Test
    @DisplayName("findAllPackageByTagNameTest")
    void findAllPackageByTagNameTest() {
        Card card1 = em.find(Card.class, 1L);
        card1.updateIsPublic(true);
        em.flush();
        
        List<PackageResponse> java = packageService.findAllPackageByTagName("java",false, PageRequest.of(0, 200));
        java.forEach(packageResponse -> {
            assertThat(packageResponse).extracting("tagName").isEqualTo("java");
        });
        assertThat(java.size()).isEqualTo(1);
        assertThat(java.get(0).getNumberOfCard()).isEqualTo(1);
    

//        List<PackageResponse> food = packageService.findAllPackageByTagName("java", PageRequest.of(0, 200));
//        logger.info("{}", food.get(0).getNumberOfCard());
//        assertThat(food.get(0).getNumberOfCard()).isEqualTo(1);
//        food.forEach(packageResponse -> {
//            assertThat(packageResponse).extracting("tagName").isEqualTo("food");
//        });
    }
    
    
}
