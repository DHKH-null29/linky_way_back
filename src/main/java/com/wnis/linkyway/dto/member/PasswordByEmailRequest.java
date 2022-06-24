package com.wnis.linkyway.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.wnis.linkyway.validation.ValidationGroup.*;

@Getter
@NoArgsConstructor
public class PasswordByEmailRequest extends PasswordRequest {

    @NotBlank(message = "email을 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z\\d]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "email 형식을 확인해주세요",
            groups = PatternCheckGroup.class)
    private String email;

    public PasswordByEmailRequest(String email, String password) {
        super(password);
        this.email = email;
    }
}
