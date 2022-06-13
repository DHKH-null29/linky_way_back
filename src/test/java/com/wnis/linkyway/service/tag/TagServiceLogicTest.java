package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/tag-test.sql")
@Import(TagServiceImpl.class)
public class TagServiceLogicTest {

    @Autowired
    TagService tagService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nested
    @DisplayName("태그 추가")
    class AddTagTest {

        @ParameterizedTest
        @CsvSource(value = { "fire,false,1", "hello21,true,1", "hello32,false,1" })
        void addTagSuccessTest(String tagName, String isPublic, Long memberId) {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName(tagName)
                                              .isPublic(isPublic)
                                              .build();
            TagResponse tagResponse = tagService.addTag(tagRequest, memberId);
            logger.info("tagId: {}, tagName: {}", tagResponse.getTagId(), tagResponse.getTagName());
        }
    }

    @Nested
    @DisplayName("태그 수정")
    class SetTagTest {

        @ParameterizedTest
        @CsvSource(value = { "source,true,1", "hot,true,2" })
        void addTagSuccessTest(String tagName, String shareable, Long tagId) {
            TagRequest tagRequest = TagRequest.builder()
                                              .tagName(tagName)
                                              .isPublic(shareable)
                                              .build();
            TagResponse tagResponse = tagService.setTag(tagRequest, tagId);
            logger.info("tagId: {}, tagName: {}", tagResponse.getTagId(), tagResponse.getTagName());
        }
    }

    @Nested
    @DisplayName("태그 삭제")
    class DeleteTagTest {

        @ParameterizedTest
        @CsvSource(value = { "1", "2", }, delimiter = ',')
        void addTagSuccessTest(Long tagId) {
            TagResponse tagResponse = tagService.deleteTag(tagId);
            logger.info("tagId: {}, tagName: {}", tagResponse.getTagId(), tagResponse.getTagName());
        }
    }

    @Nested
    @DisplayName("태그 조회")
    class FindTagTest {

        @ParameterizedTest
        @CsvSource(value = { "1", "2", })
        void findTagSuccessTest() {
            List<TagResponse> tagResponseList = tagService.searchTags(1L);

        }
    }
}
