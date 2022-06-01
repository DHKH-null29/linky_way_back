package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotAddDuplicateEntityException;
import com.wnis.linkyway.exception.common.NotDeleteEmptyEntityException;
import com.wnis.linkyway.exception.common.NotModifyEmptyEntityException;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public Response searchTags(Long memberId) {
        List<TagResponse> tagList = tagRepository.findAllTagList(memberId);
        return Response.of(HttpStatus.OK, tagList, "성공적으로 태그를 조회했습니다.");
    }
    
    @Override
    public Response addTag(TagRequest tagRequest, Long memberId) {
        if (tagRepository.existsByNameAndId(tagRequest.getTagName(), memberId)) {
            throw new NotAddDuplicateEntityException("중복된 태그 이름을 추가 할 수 없습니다");
        }
        
        Tag tag = Tag.builder().name(tagRequest.getTagName()).shareable(Boolean.parseBoolean(tagRequest.getShareable()))
                .views(0).member(memberRepository.getById(memberId)).build();
        
        tagRepository.save(tag);
        TagResponse tagResponse = TagResponse.builder()
                .tagId(tag.getId())
                .build();
        
        return Response.of(HttpStatus.OK, tagResponse, "태그가 성공적으로 생성되었습니다.");
    }
    
    @Override
    public Response setTag(TagRequest tagRequest, Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() ->
                new NotModifyEmptyEntityException("태그가 존재하지 않아 수정 할 수 없습니다.")
        );
        
        tag.updateName(tagRequest.getTagName()).updateShareable(Boolean.parseBoolean(tagRequest.getShareable()));
        return Response.of(HttpStatus.OK, null, "태그가 성공적으로 수정되었습니다");
    }
    
    @Override
    public Response deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new NotDeleteEmptyEntityException("태그가 존재하지 않아 삭제 할 수 없습니다");
        }
        
        tagRepository.deleteById(tagId);
        return Response.of(HttpStatus.OK, null, "성공적으로 삭제되었습니다");
    }
    
}
