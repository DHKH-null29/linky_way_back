package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;

public interface CardService {

    public AddCardResponse addCard(Long memberId, CardRequest cardRequest);

    public CardResponse findCardByCardId(Long cardId);
    
    public Card updateCard(Long memberId, Long cardId, CardRequest cardRequest);
    
    public void deleteCard(Long cardId);
}
