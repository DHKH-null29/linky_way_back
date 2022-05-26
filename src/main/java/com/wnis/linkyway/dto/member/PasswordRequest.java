package com.wnis.linkyway.dto.member;


import com.wnis.linkyway.validation.ValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PasswordRequest {
    
    @NotBlank(message = "password를 입력해주세요", groups = ValidationGroup.NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W)(?=\\S+$).{4,16}$",
            message = "최소 대/소 문자 하나, 숫자 하나, 특수문자를 4 ~ 16 글자로 입력해주세요"
            ,groups = ValidationGroup.PatternCheckGroup.class)
    String password;
}
