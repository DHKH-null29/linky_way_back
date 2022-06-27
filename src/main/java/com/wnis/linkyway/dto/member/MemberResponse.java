package com.wnis.linkyway.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.entity.Member;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    
    
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
    
    public static MemberResponse of(Long memberId, String email, String nickname ) {
        return MemberResponse.builder()
                .memberId(memberId)
                .email(email)
                .nickname(nickname)
                .build();
    }
    
    
}
