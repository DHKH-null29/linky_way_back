package com.wnis.linkyway.dto.card.io;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.dto.tag.TagResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CardResponse {

    private Long cardId;

    private String link;

    private String title;

    private String content;
    
    private Long folderId;

    private Boolean isPublic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    private List<TagResponse> tags = new ArrayList<>();

    @Builder
    private CardResponse(Long cardId, String link, String title, String content, Long folderId, boolean isPublic,
            List<TagResponse> tags, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.cardId = cardId;
        this.link = link;
        this.title = title;
        this.content = content;
        this.folderId = folderId;
        this.isPublic = isPublic;
        if (tags != null)
            this.tags = tags;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

}
