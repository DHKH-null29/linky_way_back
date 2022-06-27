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

    public CardResponse findCardByCardId(Long cardId, Long memberId);

    public Long updateCard(Long memberId, Long cardId, CardRequest cardRequest);

    public Long deleteCard(Long cardId, Long memberId);

    public List<CardResponse> SearchCardByKeywordPersonalPage(String keyword, Long memberId, Pageable pageable);

    public List<CardResponse> findCardsByTagId(Long memberId, Long tagId, Pageable pageable);

    public List<SocialCardResponse> findIsPublicCardsByTagId(Long tagId, Pageable pageable);

    public List<CardResponse> findCardsByFolderId(Long memberId, Long folderId, boolean findDeep, Pageable pageable);

    public List<CardResponse> findCardsByMemberId(Long memberId, Pageable pageable);

    public int copyCardsInPackage(CopyPackageCardsRequest copyPackageCardsRequest);
}
