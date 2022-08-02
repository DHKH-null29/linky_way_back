package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.Page;
import com.wnis.linkyway.dto.card.io.AddCardResponse;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.card.io.CopyPackageCardsRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CardService {

    AddCardResponse addCard(Long memberId, CardRequest cardRequest);

    CardResponse findCardByCardId(Long cardId, Long memberId);

    Long updateCard(Long memberId, Long cardId, CardRequest cardRequest);

    Long deleteCard(Long cardId, Long memberId);

    Page<CardResponse> SearchCardByKeywordPersonalPage(Long lastIdx, String keyword,
        Long memberId, Pageable pageable);

    Page<CardResponse> findCardsByTagId(Long lastIdx, Long memberId, Long tagId,
        Pageable pageable);

    Page<CardResponse> findCardsByFolderId(Long lastIdx, Long memberId, Long folderId, boolean findDeep,
        Pageable pageable);

    Page<CardResponse> findCardsByMemberId(Long lastIdx, Long memberId, Pageable pageable);

    int copyCardsInPackage(CopyPackageCardsRequest copyPackageCardsRequest);
}
