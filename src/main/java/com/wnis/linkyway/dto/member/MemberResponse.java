package com.wnis.linkyway.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
}
