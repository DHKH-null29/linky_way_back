package com.wnis.linkyway.dto.card;

import com.wnis.linkyway.entity.Tag;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
public class CardDto {

    private Long id;
    private String link;
    private String title;
    private String content;
    private Boolean isPublic;
    private Boolean isDeleted;
    private Long folderId;

    private Set<Tag> tagSet = new HashSet<>();

    public CardDto() {}

    @Builder
    public CardDto(Long id, String link, String title, String content, Boolean isPublic,
        Boolean isDeleted, Long folderId) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.isDeleted = isDeleted;
        this.folderId = folderId;
    }
}