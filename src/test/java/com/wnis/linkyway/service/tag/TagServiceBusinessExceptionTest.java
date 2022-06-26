package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TagServiceBusinessExceptionTest {

    @Mock
    TagRepository tagRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    TagServiceImpl tagService;

    @Nested
    @DisplayName("태그 추가")
    class AddTagTest {

        @Test
        @DisplayName("중복 입력시 예외")
        void shouldThrowDuplicateExceptionWhenTagNameDuplicated() {
            doReturn(true).when(memberRepository)
                          .existsById(any()); // 회원 검증 통과
            doReturn(true).when(tagRepository)
                          .existsByMemberIdAndTagName(any(), any()); // 중복 입력 예외

            Assertions.assertThatThrownBy(() -> tagService.addTag(TagRequest.builder()
                                                                            .build(),
                                                                  1L))
                      .isInstanceOf(NotAddDuplicateEntityException.class);
        }

        @Test
        @DisplayName("엉뚱한 회원 입력시 예외")
        void shouldThrowNotFoundExceptionWhenMemberIdNotFound() {
            doReturn(false).when(memberRepository)
                           .existsById(any());

            Assertions.assertThatThrownBy(() -> {
                tagService.addTag(TagRequest.builder()
                                            .build(),
                                  1L);
            })
                      .isInstanceOf(NotFoundEntityException.class);
        }
    }

    @Nested
    @DisplayName("태그 수정")
    class SetTagTest {

        @Test
        @DisplayName("데이터가 없이 수정시 예외")
        void shouldThrowNotModifyExceptionWhenHasNoTag() {
            doReturn(Optional.empty()).when(tagRepository)
                                      .findById(any());

            Assertions.assertThatThrownBy(() -> {
                tagService.setTag(TagRequest.builder()
                                            .build(),
                                  1L, 1L);
            })
                      .isInstanceOf(NotModifyEmptyEntityException.class);
        }

        
    }

    @Nested
    @DisplayName("태그 삭제")
    class DeleteTagTest {

        @Test
        @DisplayName("데이터가 없는데 삭제시 예외")
        void shouldThrowNotDeleteExceptionWhenHasNoTag() {
            doReturn(Optional.empty()).when(tagRepository)
                           .findById(any());

            Assertions.assertThatThrownBy(() -> {
                tagService.deleteTag(1L, 1L);
            })
                      .isInstanceOf(NotFoundEntityException.class);
        }
    }
}
