package com.wnis.linkyway.service;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrashService {
    
    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    private final MemberRepository memberRepository;
    
    public List<Long> updateDeleteCardTrueOrFalse(List<Long> ids, Long memberId, boolean isDeleted) {
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
    
}
