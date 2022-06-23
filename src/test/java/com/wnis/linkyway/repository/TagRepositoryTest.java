package com.wnis.linkyway.repository;

import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/tag-test.sql")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    private final long VALID_MEMBER_ID = 1L;
    
    @ParameterizedTest
    @CsvFileSource(resources = "/input/tag/repository/findAllTagList-test.csv", delimiter = ',', numLinesToSkip = 1)
    void findAllTagListTest(Long memberId) {
        List<TagResponse> tagList = tagRepository.findAllTagList(memberId);
        logger.info("size: {}", tagList.size());
        tagList.forEach((t) -> logger.info("tag id: {}, tag name: {}", t.getTagId(), t.getTagName()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/input/tag/repository/existsByTagNameAndMemberId-test.csv",
                   delimiter = ',',
                   numLinesToSkip = 1)
    void existsByTagNameAndMemberIdTest(String name, Long memberId) {
        boolean bool = tagRepository.existsByMemberIdAndTagName(name, memberId);
        logger.info("result: {}", bool);
    }
    
    @Test
    void countTagTest() {
        long numOfTag = tagRepository.countTagByMemberId(VALID_MEMBER_ID);
        assertThat(numOfTag).isEqualTo(4);
    }
    
    @Test
    @DisplayName("findAllDistinctTagList 테스트")
    void findAllDistinctTagListTest() {
        List<Tag> spring = tagRepository.findAllDistinctTagListByTagName("spring", PageRequest.of(0, 2));
        assertThat(spring.size()).isEqualTo(2);
        for (Tag tag : spring) {
            logger.info("id: {}, name: {}", tag.getId(), tag.getName());
            
        }
    }
}