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
        
        // 회원이 가지고 있는 편집할 id 목록 조회해서 업데이트
        List<Card> cardList = cardRepository.findAllInIdsAndMemberId(ids, memberId);
        for (Card card : cardList) {
            card.updateIsDeleted(isDeleted);
            result.add(card.getId());
        }
        return result;
    }
    
    public List<CardResponse> findAllDeletedCard(Long memberId, Long lastCardId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원이 존재하지 않습니다");
        }
        List<Card> cardList = null;
        
        // lastCardId 상태에 따라 최초 페이징이냐 커서페이징이냐 결정
        if (checkInvalidLastCardId(lastCardId)) {
            cardList = cardRepository.findAllByIsDeletedAndMemberIdUsingPage(true,
                                                                             memberId,
                                                                             PageRequest.of(0, pageable.getPageSize()));
        } else {
            cardList = cardRepository.findAllByIsDeletedAndMemberIdUsingCursorPage(true,
                                                                                   lastCardId,
                                                                                   memberId,
                                                                                   PageRequest.of(0, pageable.getPageSize()));
        }
        
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
    
    private boolean checkInvalidLastCardId(Long lastId) {
        return lastId == null || lastId == 1L;
    }
}
