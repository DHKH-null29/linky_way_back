package com.wnis.linkyway.dto.card;

import com.wnis.linkyway.validation.ValidationGroup.NotBlankGroup;

import lombok.*;

import javax.validation.constraints.NotNull;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CopyPackageCardsRequest {

    @NotNull(message = "복사할 태그가 존재하지 않습니다.", groups = NotBlankGroup.class)
    private Long tagId;

    @NotNull(message = "폴더아이디는 필수입니다.", groups = NotBlankGroup.class)
    private Long folderId;
    
    @NotNull(message = "소셜 공유 여부를 입력해주세요", groups = NotBlankGroup.class)
    private boolean isPublic;

    @NotNull(message = "복사할 카드가 존재하지 않습니다.", groups = NotBlankGroup.class)
    private List<CopyCardsRequest> copyCardsRequestList;
}
