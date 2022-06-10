package com.wnis.linkyway.dto.member;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.wnis.linkyway.validation.ValidationGroup.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinRequest {

    @NotBlank(message = "이메일을 입력해주세요", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z\\d]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
             message = "이메일 형식을 확인해주세요",
             groups = PatternCheckGroup.class)
    private String email;

    @NotBlank(message = "패스워드를 입력해주세요", groups = NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W)(?=\\S+$).{4,16}$",
             message = "최소 대/소 문자 하나, 숫자 하나, 특수문자를 4 ~ 16 글자로 입력해주세요",
             groups = PatternCheckGroup.class)
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z\\d_]{2,10}$",
             message = "문자/숫자로만 2~10 글자 사이로 입력해주세요",
             groups = PatternCheckGroup.class)
    private String nickname;
}
