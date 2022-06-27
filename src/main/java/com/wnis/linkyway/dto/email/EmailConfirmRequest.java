package com.wnis.linkyway.dto.email;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.wnis.linkyway.validation.ValidationGroup.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EmailConfirmRequest {

    @NotBlank(message = "이메일을 입력해주세요", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z\\d]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 확인해주세요",
            groups = PatternCheckGroup.class)
    private String email;

    @NotBlank(message = "인증 코드를 입력하세요", groups = NotBlankGroup.class)
    private String code;

}
