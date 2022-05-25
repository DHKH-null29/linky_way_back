package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;

public interface CardService {

    public Long addCard(CardRequest cardRequest);

    public CardResponse findCardByCardId(Long cardId);
}
