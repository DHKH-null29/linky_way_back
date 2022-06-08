package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.*;
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
        
        if (tagRepository.existsByTagNameAndMemberId(tagRequest.getTagName(), memberId)) {
            throw new NotAddDuplicateEntityException("중복된 태그 이름을 추가 할 수 없습니다");
        }
        
        Tag tag = Tag.builder().name(tagRequest.getTagName()).shareable(Boolean.parseBoolean(tagRequest.getShareable()))
                .views(0).member(memberRepository.getById(memberId)).build();
        
        tagRepository.save(tag);
        return TagResponse.builder()
                .tagId(tag.getId())
                .build();
    }
    
    @Override
    public TagResponse setTag(TagRequest tagRequest, Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() ->
                new NotModifyEmptyEntityException("태그가 존재하지 않아 수정 할 수 없습니다.")
        );
        
        if (tagRepository.existsByTagNameAndMemberId(tagRequest.getTagName(), tagId)) {
            throw new NotModifyDuplicateException("이미 존재하는 태그의 이름으로 수정 할 수 없습니다");
        }
        
        tag.updateName(tagRequest.getTagName())
                .updateShareable(Boolean.parseBoolean(tagRequest.getShareable()));
        
        return TagResponse.builder().tagId(tag.getId())
                .tagName(tag.getName()).shareable(tag.getShareable())
                .build();
    }
    
    @Override
    public TagResponse deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new NotDeleteEmptyEntityException("태그가 존재하지 않아 삭제 할 수 없습니다");
        }
        
        tagRepository.deleteById(tagId);
        return TagResponse.builder().tagId(tagId).build();
    }
    
}
