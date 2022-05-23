package com.wnis.linkyway.service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TagServiceImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
class TagServiceImplTest {
    
    @Autowired
    TagService tagService;
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TagRepository tagRepository;
    
    @Autowired
    EntityManager entityManager;
    
    @Nested
    @DisplayName("태그 추가 테스트")
    class addTagTest {
        
        @Test
        @DisplayName("중복에 의한 예외 상황")
        void failTest() {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("spring")
                    .build();
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag = Tag.builder()
                    .member(member)
                    .name("spring")
                    .shareable(true)
                    .build();
            
            tagRepository.save(tag);
            memberRepository.save(member);
            assertThatThrownBy(() -> {
                tagService.addTag(tagRequest, 1L);
            }).isInstanceOf(ResourceConflictException.class);
            
        }
        
        @Test
        @DisplayName("성공 테스트")
        void successTest() {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("spring")
                    .build();
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag = Tag.builder()
                    .member(member)
                    .name("food")
                    .shareable(true)
                    .build();
            
            memberRepository.save(member);
            tagRepository.save(tag);
            
            
            tagService.addTag(tagRequest, 1L);
            tagRepository.findAll().forEach((t) -> {
                assertThat(t).isNotNull();
                assertThat(t.getMember().getId()).isEqualTo(1L);
            });
        }
    }
    
    @Nested
    @DisplayName("태그 수정 테스트")
    class setTagTest {
        
        @Test
        @DisplayName("수정 성공한 경우")
        void successTest() {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("spring")
                    .shareable("true")
                    .build();
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag = Tag.builder()
                    .member(member)
                    .name("food")
                    .shareable(true)
                    .build();
            tagRepository.save(tag);
            
            tagService.setTag(tagRequest, 1L);
            assertThat(tagRepository.findById(1L).get()).isInstanceOfSatisfying(Tag.class, (t) -> {
                assertThat(tag.getName()).isEqualTo("spring");
                assertThat(tag.getShareable()).isEqualTo(true);
            });
        }
        
        @Test
        @DisplayName("데이터가 없어서 수정 실패한 경우")
        void failTest() {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("spring")
                    .shareable("true")
                    .build();
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag = Tag.builder()
                    .member(member)
                    .name("food")
                    .shareable(true)
                    .build();
            tagRepository.save(tag);
            
            Assertions.assertThatThrownBy(() -> {
                tagService.setTag(tagRequest, 2L);
            }).isInstanceOf(ResourceConflictException.class);
        }
    }
    
    @Nested
    @DisplayName("태그 삭제 테스트")
    class DeleteTagTest {
        
        @Test
        @DisplayName("삭제 성공")
        void successTest() {
            TagRequest tagRequest = TagRequest.builder()
                    .tagName("spring")
                    .shareable("true")
                    .build();
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag1 = Tag.builder()
                    .name("food")
                    .shareable(true)
                    .build();
            
            Tag tag2 = Tag.builder()
                    .name("food2")
                    .shareable(true)
                    .build();
            
            member.addTag(tag1);
            member.addTag(tag2);
            
            memberRepository.save(member);
            tagRepository.save(tag1);
            tagRepository.save(tag2);
            
            tagService.deleteTag(1L);
            assertThat(tagRepository.findAll().size()).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("태그 조회 테스트")
    class SearchTagsTest {
        
        @Test
        @DisplayName("태그 조회 테스트")
        void successTest() throws JsonProcessingException {
            Member member = Member.builder()
                    .email("hello@naver.com")
                    .nickname("aasd12")
                    .password("a!asdD@12321")
                    .build();
            Tag tag1 = Tag.builder()
                    .name("food")
                    .shareable(true)
                    .build();
            
            Tag tag2 = Tag.builder()
                    .name("food2")
                    .shareable(false)
                    .build();
            
            Tag tag3 = Tag.builder()
                    .name("food3")
                    .shareable(true)
                    .build();
            
            member.addTag(tag1)
                    .addTag(tag2)
                    .addTag(tag3);
            
            memberRepository.save(member);
            tagRepository.saveAll(Arrays.asList(tag1, tag2, tag3));
            
            Response tagResponseList = tagService.searchTags(1L);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(tagResponseList);
            System.out.println(s);
        }
    }
}