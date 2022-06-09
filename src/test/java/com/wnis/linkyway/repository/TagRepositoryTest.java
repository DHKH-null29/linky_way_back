package com.wnis.linkyway.repository;

import com.wnis.linkyway.dto.tag.TagResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/tag-test.sql")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        boolean bool = tagRepository.existsByTagNameAndMemberId(name, memberId);
        logger.info("result: {}", bool);
    }
}