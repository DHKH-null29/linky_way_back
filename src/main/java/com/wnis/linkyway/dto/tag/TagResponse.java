package com.wnis.linkyway.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.entity.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TagResponse {

    private Long tagId;
    private String tagName;
    private Boolean isPublic;


    @Builder
    private TagResponse(Long tagId, String tagName, Boolean isPublic) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.isPublic = isPublic;
    }

    public TagResponse(Tag tag) {
        this.tagId = tag.getId();
        this.tagName = tag.getName();
        this.isPublic = tag.getIsPublic();
    }
}
