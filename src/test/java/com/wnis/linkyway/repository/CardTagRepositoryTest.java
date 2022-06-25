package com.wnis.linkyway.repository;

import com.wnis.linkyway.dto.PackageDto;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
class CardTagRepositoryTest {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired EntityManager em;
    @Autowired CardTagRepository cardTagRepository;
    @Autowired CardRepository cardRepository;
    
    Member member = Member.builder()
                          .email("h")
                          .nickname("hello")
                          .password("1")
                          .build();
    
    Folder folder = Folder.builder()
                          .member(member)
                          .name("1")
                          .depth(1L)
                          .build();
    
    Tag tag1 = Tag.builder()
                  .name("t1")
                  .isPublic(false)
                  .member(member)
                  .build();
    
    Tag tag2 = Tag.builder()
                  .name("t2")
                  .isPublic(false)
                  .member(member)
                  .build();
    
    Card card = Card.builder()
                    .folder(folder)
                    .isPublic(false)
                    .content("")
                    .link("asda")
                    .title("card")
                    .build();
    
    Card card2 = Card.builder()
            .folder(folder)
            .isPublic(false)
            .content("")
            .link("2")
            .title("card")
            .build();
    
    CardTag cardTag1 = CardTag.builder()
                              .card(card)
                              .tag(tag1)
                              .build();
    
    CardTag cardTag2 = CardTag.builder()
                              .card(card)
                              .tag(tag2)
                              .build();
    
    CardTag cardTag3 = CardTag.builder()
            .card(card2)
            .tag(tag2)
            .build();
    
    @BeforeEach
    void setUp() {
        em.persist(member);
        em.persist(folder);
        em.persist(tag1);
        em.persist(tag2);
        em.persist(card);
        em.persist(card2);
        em.persist(cardTag1);
        em.persist(cardTag2);
        em.persist(cardTag3);
        em.flush();
    }
    
    @Nested
    @DisplayName("CardTag 연관관계 테스트")
    class UpdateCardTagTest {
        
        
        @Test
        @DisplayName("카드 업데이트 시 카드 태그 결과")
        void resultCardTagWhenUpdateCard() {
            em.clear();
            Card card1 = cardRepository.findById(1L).orElse(null);
            card1.updateTitle("updatedCard");
            logger.info("{}", card1.getTitle());
            cardRepository.saveAndFlush(card1);
            em.clear();
    
            Card card2 = cardRepository.findById(1L).orElse(null);
            logger.info(card2.getTitle());
            assertThat(card1.getTitle()).isEqualTo(card2.getTitle());
        
        }
        
        
    }
    
    @Nested
    @DisplayName("findAllTagIdByCardId 테스트")
    class findAllTagIdByCardIdTest {
        
        @Test
        @DisplayName("Set<tagId> 리턴 테스트")
        void returnTagIdSetWhenCallFindAllTagIdByCardId() {
            Set<Long> tagIdSet = cardTagRepository.findAllTagIdByCardId(1L);
            assertThat(tagIdSet.contains(1L)).isEqualTo(true);
            assertThat(tagIdSet.contains(2L)).isEqualTo(true);
        }
    }
    
    @Nested
    @DisplayName("findAllTagResponseByCardId 테스트")
    class findAllTagResponseByCardIdTest {
        
        @Test
        @DisplayName("List<TagResponse> 응답 테스트")
        void shouldReturnTagResponseTest() {
            List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(1L);
            tags.forEach(tagResponse -> {
                assertThat(tagResponse).isInstanceOf(TagResponse.class);
                logger.info("id: {}, name: {}, is_public: {}", tagResponse.getTagId(),
                            tagResponse.getTagName(),
                            tagResponse.getIsPublic());
            });
            
        }
    }
    
    @Nested
    @DisplayName("findAllPackageDtoByTagName 테스트")
    class findAllPackageDtoByTagName {
        
        @Test
        @DisplayName("응답 테스트")
        void shouldReturnListPackageDtoTest() {
            List<PackageDto> packageDtoList = cardTagRepository.findAllPackageDtoByTagName("t1");
            assertThat(packageDtoList).isNotEmpty();
            assertThat(packageDtoList.get(0)).extracting("tagName").isEqualTo("t1");
            assertThat(packageDtoList.get(0)).extracting("nickname").isEqualTo("hello");
        }
    }
    
    @Nested
    @DisplayName("findAllCardTagIdInCardIds 테스트")
    class findAllCardTagIdINCardIdsTest {
        
        @Test
        @DisplayName("응답 테스트")
        void shouldReturnCardIds() {
            List<CardTag> all = cardTagRepository.findAll();
            assertThat(all.size()).isEqualTo(3);
            List<Long> ids = cardTagRepository.findAllCardTagIdInCardIds(
                    new ArrayList<>(Arrays.asList(1L)));
            logger.info("ids: {} ", ids);
            assertThat(ids.size()).isEqualTo(2);
            assertThat(ids.get(0)).isEqualTo(1L);
    
            
        }
    }
    
}