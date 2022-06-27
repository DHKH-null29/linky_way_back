package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;

import java.util.List;

public interface TagService {

    List<TagResponse> searchTags(Long memberId);

    TagResponse addTag(TagRequest tagRequest, Long memberId);

    TagResponse setTag(TagRequest tagRequest, Long tagId, Long memberId);

    TagResponse deleteTag(Long tagId, Long memberId);

}
