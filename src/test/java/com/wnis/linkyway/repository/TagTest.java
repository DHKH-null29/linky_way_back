package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TagTest {
    @Autowired
    TagRepository tagRepository;

    @Test
    void TagMemberRelationTest() {
        Member member = Member.builder()
                .nickname("HelloWorld")
                .build();

        Tag tag = Tag.builder()
                .name("spring")
                .shareable(true)
                .member(member)
                .build();

        tagRepository.save(tag);
        List<Tag> tagList = tagRepository.findAllIncludesTag();
        assertThat(tagList.get(0).getMember().getNickname()).isEqualTo("HelloWorld");
    }
}
