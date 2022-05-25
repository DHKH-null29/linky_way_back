package com.wnis.linkyway.service.card;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public AddCardResponse addCard(CardRequest cardRequest) {
        Long cardId = cardRepository.save(cardRequest.toEntity()).getId();
        AddCardResponse addCardResponse = AddCardResponse.builder()
                                                         .cardId(cardId)
                                                         .build();
        return addCardResponse;
    }
}
