package com.wnis.linkyway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public Long addCard(CardRequest cardRequest) {
        return cardRepository.save(cardRequest.toEntity()).getId();
    }
}
