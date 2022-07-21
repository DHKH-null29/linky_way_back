package com.wnis.linkyway.dto.card.io;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AddCardResponse {

    private final Long cardId;
}
