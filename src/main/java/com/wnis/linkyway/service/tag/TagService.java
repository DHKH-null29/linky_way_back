package com.wnis.linkyway.service.tag;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;

public interface TagService {
    
    Response searchTags(Long memberId);
    
    Response addTag(TagRequest tagRequest, Long memberId);
    
    Response setTag(TagRequest tagRequest, Long tagId);
    
    Response deleteTag(Long tagId);
    
}
