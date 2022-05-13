package com.wnis.linkyway.dto.card;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.entity.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CardResponse {

    private Long cardId;
    private String link;
    private String title;
    private String content;
    private Boolean shareable;
    private List<RelatedTag> tags = new ArrayList<>();

    @Builder
    private CardResponse(Long cardId, String link, String title, String content,
                        boolean shareable, List<Tag> tags) {
        this.cardId = cardId;
        this.link = link;
        this.title = title;
        this.content = content;
        this.shareable = shareable;
        if (tags != null) {
            this.tags = tags.stream().map((tag) -> new RelatedTag(tag))
                    .collect(Collectors.toList());
        }

    }

    @Getter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class RelatedTag {

        private Long tagId;
        private String name;
        private Boolean shareable;
        private Integer views;

        public RelatedTag (Tag tag) {
            this.tagId = tag.getId();
            this.name = tag.getName();
            this.shareable = tag.getShareable();
            this.views = tag.getViews();
        }
    }

}
