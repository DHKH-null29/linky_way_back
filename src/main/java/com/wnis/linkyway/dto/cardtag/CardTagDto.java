package com.wnis.linkyway.dto.cardtag;


import lombok.Builder;
import lombok.Data;

@Data
public class CardTagDto {

    private final Long cardId;
    private final String link;
    private final String title;
    private final String content;
    private final Boolean isPublic;
    private final Long folderId;
    private final Boolean isDeleted;

    private final Long tagId;
    private final String tagName;

    @Builder
    public CardTagDto(Long cardId, String link, String title, String content, Boolean isPublic, Long folderId,
        Boolean isDeleted, Long tagId, String tagName) {
        this.cardId = cardId;
        this.link = link;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.folderId = folderId;
        this.isDeleted = isDeleted;
        this.tagId = tagId;
        this.tagName = tagName;
    }
}
