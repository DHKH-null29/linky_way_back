package com.wnis.linkyway.dto.card.io;

import static com.wnis.linkyway.validation.ValidationGroup.NotBlankGroup;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CopyCardsRequest {

    @NotBlank(message = "링크를 입력해주세요", groups = NotBlankGroup.class)
    private String link;

    @Size(max = 15)
    private String title;

    private String content;

    public Card toEntity(Folder folder, boolean isPublic) {
        return Card.builder()
            .link(link)
            .title(title)
            .content(content)
            .isPublic(isPublic)
            .folder(folder)
            .build();
    }
}
