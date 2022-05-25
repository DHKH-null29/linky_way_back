package com.wnis.linkyway.service.card;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
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

    @Override
    public CardResponse findCardByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(
                                          () -> new ResourceNotFoundException(
                                                  "해당 카드가 존재하지 않습니다."));

        return CardResponse.builder()
                           .cardId(card.getId())
                           .link(card.getLink())
                           .title(card.getTitle())
                           .content(card.getContent())
                           .shareable(card.getShareable())
                           .build();
    }

    @Override
    @Transactional
    public void updateCard(Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(
                                          () -> new ResourceConflictException(
                                                  "해당 카드가 존재하지 않아 수정이 불가능합니다."));
        card.updateLink(cardRequest.getLink());
        card.updateTitle(cardRequest.getTitle());
        card.updateContent(cardRequest.getContent());
        card.updateShareable(cardRequest.getShareable());
    }
}
