package com.wnis.linkyway.dto.member;

import com.wnis.linkyway.validation.ValidationGroup;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateMemberRequest {
    
    @NotBlank(message = "닉네임을 입력해주세요", groups = ValidationGroup.NotBlankGroup.class)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z\\d_]{2,10}$",
            message = "문자/숫자로만 2~10 글자 사이로 입력해주세요",
            groups = ValidationGroup.PatternCheckGroup.class)
    private String nickname;
}
