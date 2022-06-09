package com.wnis.linkyway.dto.tag;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.wnis.linkyway.validation.ValidationGroup.NotBlankGroup;
import static com.wnis.linkyway.validation.ValidationGroup.PatternCheckGroup;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TagRequest {

    @NotBlank(message = "소셜 공유 여부를 입력해주세요", groups = NotBlankGroup.class)
    @Pattern(regexp = "(true|false)", groups = PatternCheckGroup.class)
    String shareable;

    @NotBlank(message = "tagName을 입력해주세요", groups = NotBlankGroup.class)
    @Size(max = 10, message = "10자 이하로 작성해주세요")
    private String tagName;
}
