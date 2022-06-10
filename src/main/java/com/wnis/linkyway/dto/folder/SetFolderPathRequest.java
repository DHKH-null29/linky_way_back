package com.wnis.linkyway.dto.folder;

import com.wnis.linkyway.validation.ValidationGroup.NotBlankGroup;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SetFolderPathRequest {

    @NotNull(message = "타겟 상위 폴더의 id를 입력해주세요", groups = NotBlankGroup.class)
    @Positive(message = "targetFolderId는 1 이상의 id를 입력하셔야 합니다.")
    private Long targetFolderId;
}
