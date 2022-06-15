package com.wnis.linkyway.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.dto.tag.TagResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CardResponse {

    private Long cardId;
    private String link;
    private String title;
    private String content;
    private Boolean isPublic;
    
    private Boolean isDeleted;
    private List<TagResponse> tags = new ArrayList<>();

    @Builder
    private CardResponse(Long cardId, String link, String title, String content, boolean isPublic,boolean isDeleted, List<TagResponse> tags) {
        this.cardId = cardId;
        this.link = link;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.isDeleted = isDeleted;
        if (tags != null)
            this.tags = tags;
    }
    
}
