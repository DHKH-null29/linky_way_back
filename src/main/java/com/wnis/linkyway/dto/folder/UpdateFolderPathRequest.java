package com.wnis.linkyway.dto.folder;

import lombok.*;

import javax.validation.constraints.Positive;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UpdateFolderPathRequest {

    @Positive(message = "올바른 목표 폴더 id를 입력하세요")
    private Long targetFolderId;
}
