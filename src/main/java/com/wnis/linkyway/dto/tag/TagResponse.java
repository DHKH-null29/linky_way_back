package com.wnis.linkyway.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.entity.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TagResponse {
    
    private Long tagId;
    private String tagName;
    private Boolean shareable;
    private Integer views;
    
    @Builder
    private TagResponse(Long tagId, String tagName,
                        Boolean shareable, Integer views) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.shareable = shareable;
        this.views = views;
    }
    
    public TagResponse(Tag tag) {
        this.tagId = tag.getId();
        this.tagName = tag.getName();
        this.shareable = tag.getShareable();
        this.views = tag.getViews();
    }
}
