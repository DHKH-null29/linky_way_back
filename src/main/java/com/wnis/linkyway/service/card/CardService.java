package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;

public interface CardService {

    public AddCardResponse addCard(CardRequest cardRequest);

    public CardResponse findCardByCardId(Long cardId);
    
    public void updateCard(Long cardId, CardRequest cardRequest);
    
    public void deleteCard(Long cardId);
}
