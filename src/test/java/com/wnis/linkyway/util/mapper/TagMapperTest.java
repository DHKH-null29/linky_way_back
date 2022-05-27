package com.wnis.linkyway.util.mapper;


import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class TagMapperTest {
    private final Logger logger = LoggerFactory.getLogger(TagMapperTest.class);
    
    @Test
    @DisplayName("Tag -> AddTagResponse")
    void shouldReturnAddTagResponseFromTag() {
        Tag tag = Tag.builder().name("hello").build();
        tag.updateId(1L);
        
        TagResponse tagResponse = TagMapper.instance.tagToAddTagResponse(tag);
        assertThat(tagResponse.getTagId()).isEqualTo(1L);
        assertThat(tagResponse.getTagName()).isNull();
    }
}