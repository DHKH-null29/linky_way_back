package com.wnis.linkyway.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.dto.tag.TagResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SocialCardResponse {

    private Long cardId;

    private String link;

    private String title;

    private String content;

    private Boolean isPublic;

    private List<TagResponse> tags = new ArrayList<>();

    public SocialCardResponse(CardResponse cardResponse) {
        this.cardId = cardResponse.getCardId();
        this.link = cardResponse.getLink();
        this.title = cardResponse.getTitle();
        this.content = cardResponse.getContent();
        this.isPublic = cardResponse.getIsPublic();
        this.tags = cardResponse.getTags();
    }

}
