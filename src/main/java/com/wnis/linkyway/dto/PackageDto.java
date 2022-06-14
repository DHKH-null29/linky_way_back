package com.wnis.linkyway.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PackageDto {
    
    private Long memberId;
    private String nickname;
    private Long tagId;
    private String tagName;
    
}
