package com.wnis.linkyway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageResponse {
    
    private Long memberId;
    private String nickname;
    private Long tagId;
    private String tagName;
    private Long numberOfCard;
}
