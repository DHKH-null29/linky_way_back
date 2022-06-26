package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotAddDuplicateEntityException;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.exception.common.NotModifyEmptyEntityException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("tagService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final MemberRepository memberRepository;

    @Override
    public List<TagResponse> searchTags(Long memberId) {
        return tagRepository.findAllTagList(memberId);

    }

    @Override
    public TagResponse addTag(TagRequest tagRequest, Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원을 찾을 수 없습니다");
        }

        if (tagRepository.existsByMemberIdAndTagName(tagRequest.getTagName(), memberId)) {
            throw new NotAddDuplicateEntityException("중복된 태그 이름을 추가 할 수 없습니다");
        }

        Tag tag = Tag.builder()
                     .name(tagRequest.getTagName())
                     .isPublic(Boolean.parseBoolean(tagRequest.getIsPublic()))
                     .member(memberRepository.getById(memberId))
                     .build();

        tagRepository.save(tag);
        return TagResponse.builder()
                          .tagId(tag.getId())
                          .build();
    }

    @Override
    public TagResponse setTag(TagRequest tagRequest, Long tagId, Long memberId) {
        Tag tag = tagRepository.findById(tagId)
                               .orElseThrow(() -> new NotFoundEntityException("해당 태그가 존재하지 않아 수정 할 수 없습니다"));

        checkRequestMyTag(memberId, tag, "해당 태그가 존재하지 않아 수정 할 수 없습니다");
        
        if (tagRepository.existsByMemberIdAndTagName(tagRequest.getTagName(), tagId)) {
            throw new NotFoundEntityException("이미 존재하는 태그의 이름으로 수정 할 수 없습니다");
        }

        tag.updateName(tagRequest.getTagName())
           .updateIsPublic(Boolean.parseBoolean(tagRequest.getIsPublic()));

        return TagResponse.builder()
                          .tagId(tag.getId())
                          .tagName(tag.getName())
                          .isPublic(tag.getIsPublic())
                          .build();
    }

    @Override
    public TagResponse deleteTag(Long tagId, Long memberId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() ->
                new NotFoundEntityException("태그가 존재하지 않아 삭제 할 수 없습니다"));

        
        checkRequestMyTag(memberId, tag, "해당 태그가 존재하지 않아 삭제 할 수 없습니다");

        tagRepository.deleteById(tagId);
        return TagResponse.builder()
                          .tagId(tagId)
                          .build();
    }
    
    private void checkRequestMyTag(long memberId, Tag tag, String message) {
        if (tag.getMember().getId() != memberId) {
            throw new ResourceNotFoundException(message);
        }
    }
}
