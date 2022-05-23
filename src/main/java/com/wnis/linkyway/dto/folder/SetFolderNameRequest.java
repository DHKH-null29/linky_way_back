package com.wnis.linkyway.dto.folder;

import com.wnis.linkyway.validation.ValidationGroup.NotBlankGroup;
import com.wnis.linkyway.validation.ValidationGroup.PatternCheckGroup;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SetFolderNameRequest {
    
    @NotBlank(message = "변경하실 폴더 이름을 입력해주세요",
            groups = NotBlankGroup.class)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z\\d_]{1,10}$",
            message = "변경하실 폴더 이름은 한글,영어,숫자_ 포함 10글자 이하로 입력해주세요",
            groups = PatternCheckGroup.class)
    private String name;
    
}
