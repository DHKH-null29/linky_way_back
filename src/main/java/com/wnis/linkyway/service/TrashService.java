package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TrashService {
    
    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    private final MemberRepository memberRepository;
    
    public List<Long> updateDeleteCardTrueOrFalse(List<Long> ids, Long memberId, boolean isDeleted) {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원이 존재하지 않습니다");
        }
        List<Long> result = new ArrayList<>();
        List<Card> cardList = cardRepository.findAllInIdsAndMemberId(ids, memberId);
        for (Card card : cardList) {
            card.updateIsDeleted(isDeleted);
            result.add(card.getId());
        }
        return result;
    }
    
    public List<CardResponse> findAllDeletedCard(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원이 존재하지 않습니다");
        }

        List<Card> cardList = cardRepository.findAllByIsDeletedAndMemberId(true, memberId, pageable);
        List<CardResponse> result = new ArrayList<>();
        for (Card c : cardList) {
            List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(c.getId());
            CardResponse cardResponse = CardResponse.builder()
                    .link(c.getLink())
                    .folderId(c.getFolder().getId())
                    .cardId(c.getId())
                    .isPublic(c.getIsPublic())
                    .title(c.getTitle())
                    .content(c.getContent())
                    .tags(tags)
                    .build();
            result.add(cardResponse);
        }
        return result;
    }
}
