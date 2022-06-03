package com.wnis.linkyway.dto.card;

import com.wnis.linkyway.entity.Card;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.wnis.linkyway.validation.ValidationGroup.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardRequest {

    @NotBlank(message = "링크를 입력해주세요", groups = NotBlankGroup.class)
    private String link;

    @Size(max = 15)
    private String title;
    private String content;

    @NotNull(message = "소셜 공유 여부를 입력해주세요", groups = NotBlankGroup.class)
    private Boolean shareable;

    private Set<Long> tagIdSet;

    @NotNull(message = "폴더아이디는 필수입니다.", groups = NotBlankGroup.class)
    private Long folderId;

    public Card toEntity() {
        return Card.builder()
                   .link(link)
                   .title(title)
                   .content(content)
                   .shareable(shareable)
                   .build();
    }
}
