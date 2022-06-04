package com.wnis.linkyway.dto.folder;


import com.wnis.linkyway.validation.ValidationGroup;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AddFolderRequest {
    
    @Positive(message = "parentFolderId는 0 이상의 id를 입력하셔야 합니다.")
    private Long parentFolderId;
    
    @NotBlank(message = "추가할 폴더 이름을 입력해주세요", groups = ValidationGroup.NotBlankGroup.class)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z\\d_]{1,10}$",
            message = "추가 하실 폴더 이름은 한글,영어,숫자_ 포함 10글자 이하로 입력해주세요",
            groups = ValidationGroup.PatternCheckGroup.class)
    private String name;
}
