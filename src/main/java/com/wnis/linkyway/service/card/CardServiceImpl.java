package com.wnis.linkyway.service.card;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final TagRepository tagRepository;

    private final CardTagRepository cardTagRepository;

    @Override
    @Transactional
    public AddCardResponse addCard(CardRequest cardRequest) {
        Card savedCard = cardRepository.save(cardRequest.toEntity());

        List<TagResponse> tagResponseList = cardRequest.getTagResponseList();
        for (TagResponse tagResponse : tagResponseList) {
            Tag tag = tagRepository.findById(tagResponse.getTagId())
                                   .orElseThrow(
                                           () -> new ResourceNotFoundException(
                                                   "존재하지 않는 태그는 사용할 수 없습니다. 태그를 먼저 추가해주세요."));
            if (!cardTagRepository.findByCardAndTag(savedCard, tag)
                                  .isPresent()) {
                cardTagRepository.save(
                        CardTag.builder().card(savedCard).tag(tag).build());
            }
        }
        AddCardResponse addCardResponse = AddCardResponse.builder()
                                                         .cardId(savedCard.getId())
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
    public Card updateCard(Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(
                                          () -> new ResourceConflictException(
                                                  "해당 카드가 존재하지 않아 수정이 불가능합니다."));
        card.updateLink(cardRequest.getLink());
        card.updateTitle(cardRequest.getTitle());
        card.updateContent(cardRequest.getContent());
        card.updateShareable(cardRequest.getShareable());

        return card;
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.findById(cardId)
                      .orElseThrow(() -> new ResourceConflictException(
                              "해당 카드가 존재하지 않아 삭제가 불가능합니다."));
        cardRepository.deleteById(cardId);
    }
}
