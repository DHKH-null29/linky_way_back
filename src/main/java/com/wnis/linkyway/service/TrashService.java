package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.Page;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.exception.common.InvalidValueException;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.repository.cardtag.CardTagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrashService {

    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    private final MemberRepository memberRepository;

    public List<Long> updateDeleteCardFalse(List<Long> ids, Long memberId) {
        if (ids.isEmpty()) {
            throw new InvalidValueException("복원할 카드 id가 비어있습니다. 다시 입력해주세요");
        }

        // 회원이 가지고 있는 편집할 id 목록 조회해서 업데이트 복원
        List<Card> cardList = cardRepository.findAllInIdsAndMemberId(ids, memberId)
            .stream().filter(Card::getIsDeleted).peek(card -> card.updateIsDeleted(false))
            .collect(Collectors.toList());

        if (cardList.isEmpty()) {
            throw new ResourceNotFoundException("어떤 카드도 복원되지 않았습니다. 삭제 상태의 본인 카드 ID를 입력해주세요");
        }

        return cardList.stream().map(Card::getId).collect(Collectors.toList());
    }

    public List<Long> deleteCompletely(List<Long> ids, Long memberId) {
        if (ids.isEmpty()) {
            throw new InvalidValueException("삭제할 카드 id가 비어있습니다. 다시 입력해주세요");
        }
        // card Id 중 본인 것의 cardId만 검증해서 가져옴
        List<Long> cardIds = cardRepository.findAllInIdsAndMemberId(ids, memberId).stream()
            .filter(Card::getIsDeleted)
            .map(Card::getId).collect(Collectors.toList());

        if (cardIds.isEmpty()) {
            throw new ResourceNotFoundException("어떤 데이터도 삭제되지 않았습니다. 삭제 상태의 본인 카드 ID를 입력해주세요");
        }
        // 연관 관계 제거
        List<Long> cardTagIds = cardTagRepository.findAllCardTagIdInCardIds(cardIds);
        cardTagRepository.deleteAllCardTagInIds(cardTagIds);
        // 카드 제거
        cardRepository.deleteAllByIdInBatch(cardIds);

        return cardIds;
    }

    public Page<CardResponse> findAllDeletedCard(Long memberId, Long lastCardId,
        Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원이 존재하지 않습니다");
        }
        Slice<Card> cardList;

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

        List<CardResponse> content = new ArrayList<>();

        for (Card c : cardList.getContent()) {
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
            content.add(cardResponse);
        }
        Long lastIdx = content.isEmpty() ? null : content.get(content.size() - 1).getCardId();
        return Page.of(content, cardList.hasNext(), lastIdx);
    }

    private boolean checkInvalidLastCardId(Long lastId) {
        return lastId == null || lastId == 1L;
    }
}
