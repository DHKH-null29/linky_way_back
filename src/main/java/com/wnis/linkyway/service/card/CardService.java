package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.io.AddCardResponse;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.card.io.CopyPackageCardsRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CardService {

    public AddCardResponse addCard(Long memberId, CardRequest cardRequest);

    public CardResponse findCardByCardId(Long cardId, Long memberId);

    public Long updateCard(Long memberId, Long cardId, CardRequest cardRequest);

    public Long deleteCard(Long cardId, Long memberId);

    public List<CardResponse> SearchCardByKeywordPersonalPage(Long lastIdx, String keyword,
        Long memberId, Pageable pageable);

    public List<CardResponse> findCardsByTagId(Long lastIdx, Long memberId, Long tagId,
        Pageable pageable);

    public List<CardResponse> findCardsByFolderId(Long lastIdx, Long memberId, Long folderId, boolean findDeep,
        Pageable pageable);

    public List<CardResponse> findCardsByMemberId(Long lastIdx, Long memberId, Pageable pageable);

    public int copyCardsInPackage(CopyPackageCardsRequest copyPackageCardsRequest);
}
