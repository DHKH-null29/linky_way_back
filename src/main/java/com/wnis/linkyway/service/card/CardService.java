package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.CopyPackageCardsRequest;
import com.wnis.linkyway.dto.card.SocialCardResponse;
import com.wnis.linkyway.entity.Card;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    public AddCardResponse addCard(Long memberId, CardRequest cardRequest);

    public CardResponse findCardByCardId(Long cardId);

    public Card updateCard(Long memberId, Long cardId, CardRequest cardRequest);

    public void deleteCard(Long cardId);

    public List<CardResponse> SearchCardByKeywordPersonalPage(String keyword, Long memberId, Pageable pageable);

    public List<CardResponse> findCardsByTagId(Long memberId, Long tagId);

    public List<SocialCardResponse> findIsPublicCardsByTagId(Long tagId);

    public List<CardResponse> findCardsByFolderId(Long memberId, Long folderId, boolean findDeep);

    public List<CardResponse> findCardsByMemberId(Long memberId);

    public int copyCardsInPackage(CopyPackageCardsRequest copyPackageCardsRequest);
}
