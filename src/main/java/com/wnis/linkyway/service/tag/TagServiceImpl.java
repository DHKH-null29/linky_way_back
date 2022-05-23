package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("tagService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {
    
    private final TagRepository tagRepository;
    
    private final MemberRepository memberRepository;
    
    
    @Override
    public Response searchTags(Long memberId) {
        List<TagResponse> tagList = tagRepository.findAllTagList(memberId);
        return Response.<List<TagResponse>>builder()
                .code(200)
                .message("성공적으로 태그를 조회했습니다.")
                .data(tagList)
                .build();
    }
    
    @Override
    public Response addTag(TagRequest tagRequest, Long memberId) {
        Tag tag = tagRepository.findByTagNameAndMemberId(tagRequest.getTagName(), memberId);
        if (tag != null) {
            throw new ResourceConflictException("입력 값이 이미 존재합니다.");
        }
        tagRepository.addTag(tagRequest.getTagName(), Boolean.parseBoolean(tagRequest.getShareable()), memberId);
        Tag t = tagRepository.findByTagNameAndMemberId(tagRequest.getTagName(), memberId);
        TagResponse tagResponse = TagResponse.builder()
                .tagId(t.getId())
                .build();
        
        return Response.<TagResponse>builder()
                .code(200)
                .message(String.format("%s 태그가 성공적으로 생성되었습니다.", t.getName()))
                .data(tagResponse)
                .build();
        
    }
    
    @Override
    public Response setTag(TagRequest tagRequest, Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            throw new ResourceConflictException("태그가 존재하지 않아 수정 할 수 없습니다.");
        }
        tag.updateName(tagRequest.getTagName()).updateShareable(Boolean.parseBoolean(tagRequest.getShareable()));
        Tag t = tagRepository.save(tag);
        return Response.builder()
                .code(200)
                .message(String.format("%s 태그가 성공적으로 수정되었습니다.", t.getName()))
                .build();
    }
    
    @Override
    public Response deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            throw new ResourceConflictException("태그가 존재하지 않아 삭제 할 수 없습니다.");
        }
        tagRepository.deleteById(tagId);
        return Response.builder()
                .code(200)
                .message("성공적으로 삭제되었습니다.")
                .build();
    }
    
}
