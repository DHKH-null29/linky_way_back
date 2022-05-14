package com.wnis.linkyway.repository;



import com.wnis.linkyway.entity.Ddabong;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DdabongTest {
    @Autowired
    DdabongRepository ddabongRepository;


    @Test
    void DdabongMemberRelationTest() {
        Member member = Member.builder()
                .nickname("hello")
                .build();

        Ddabong ddabong = Ddabong.builder()
                .member(member)
                .build();

        ddabongRepository.save(ddabong);
        List<Ddabong> ddabongList = ddabongRepository.findAllIncludesMember();
        assertThat(ddabongList.size()).isEqualTo(1);
        assertThat(ddabongList.get(0).getMember().getNickname()).isEqualTo("hello");
    }

    @Test
    void DdabongTagRelationTest() {
        Tag tag = Tag.builder()
                .name("태그이름")
                .build();

        Ddabong ddabong = Ddabong.builder()
                .tag(tag)
                .build();

        ddabongRepository.save(ddabong);
        List<Ddabong> ddabongList = ddabongRepository.findAllIncludesTag();
        assertThat(ddabongList.size()).isEqualTo(1);
        assertThat(ddabongList.get(0).getTag().getName()).isEqualTo("태그이름");
    }
}
