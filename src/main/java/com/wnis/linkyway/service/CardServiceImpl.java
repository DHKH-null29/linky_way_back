package com.wnis.linkyway.service;

import org.springframework.stereotype.Service;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    
    @Override
    public Long addCard(CardRequest cardRequest) {
        return cardRepository.save(cardRequest.toEntity()).getId();
    }

}
