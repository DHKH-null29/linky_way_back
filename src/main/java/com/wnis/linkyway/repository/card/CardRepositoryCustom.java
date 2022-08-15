package com.wnis.linkyway.repository.card;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wnis.linkyway.dto.Page;
import com.wnis.linkyway.dto.card.CardDto;
import com.wnis.linkyway.entity.QCard;
import com.wnis.linkyway.entity.QCardTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QCard card = QCard.card;
    private final QCardTag cardTag = QCardTag.cardTag;

    public Page<CardDto> findAllCardByFolderIds(Long lastIdx, List<Long> folderList,
        Pageable pageable) {
        List<CardDto> cardDtoList = jpaQueryFactory.select(Projections.constructor(CardDto.class,
                card.id,
                card.link,
                card.title,
                card.content,
                card.isPublic,
                card.isDeleted,
                card.folder.id,
                card.createdBy,
                card.modifiedBy))
            .from(card)
            .orderBy(card.id.desc())
            .where(card.folder.id.in(folderList).and(cursorId(lastIdx))
                .and(card.isDeleted.eq(false)))
            .limit(pageable.getPageSize())
            .fetch();

        return makePage(cardDtoList, pageable);
    }

    public Page<CardDto> findAllCardContainKeyword(Long lastIdx, String keyword, Long memberId,
        Pageable pageable) {
        List<CardDto> cardDtoList = jpaQueryFactory.select(Projections.constructor(CardDto.class,
                card.id,
                card.link,
                card.title,
                card.content,
                card.isPublic,
                card.isDeleted,
                card.folder.id,
                card.createdBy,
                card.modifiedBy))
            .from(card)
            .orderBy(card.id.desc())
            .where(card.folder.member.id.eq(memberId)
                .and(card.title.contains(keyword).or(card.content.contains(keyword)))
                .and(cursorId(lastIdx))
                .and(card.isDeleted.eq(false)))
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return makePage(cardDtoList, pageable);
    }

    private BooleanExpression cursorId(Long lastIdx) {
        if (lastIdx == null) {
            return null;
        }
        return card.id.lt(lastIdx);
    }

    private Page<CardDto> makePage(List<CardDto> cardDtoList, Pageable pageable) {
        boolean hasNext = cardDtoList.size() > pageable.getPageSize();
        if (hasNext) {
            cardDtoList.remove(cardDtoList.size() - 1);
        }

        if (cardDtoList.isEmpty()) {
            return Page.of(cardDtoList, hasNext, null);
        }

        Long returnLastIdx = cardDtoList.get(cardDtoList.size() - 1).getId();
        return Page.of(cardDtoList, hasNext, returnLastIdx);
    }

    public Page<CardDto> findAllCardByTadId(Long lastIdx, Long tagId, Pageable pageable) {
        List<CardDto> cardDtoList = jpaQueryFactory.select(Projections.constructor(CardDto.class,
                card.id,
                card.link,
                card.title,
                card.content,
                card.isPublic,
                card.isDeleted,
                card.folder.id,
                card.createdBy,
                card.modifiedBy))
            .from(cardTag)
            .orderBy(card.id.desc())
            .join(cardTag.card, card)
            .where(cardTag.tag.id.eq(tagId)
                .and(cursorId(lastIdx))
                .and(card.isDeleted.eq(false)))
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return makePage(cardDtoList, pageable);
    }

    public Page<CardDto> findAllCardByMemberId(Long lastIdx, Long memberId, Pageable pageable) {
        List<CardDto> cardDtoList = jpaQueryFactory.select(Projections.constructor(CardDto.class,
                card.id,
                card.link,
                card.title,
                card.content,
                card.isPublic,
                card.isDeleted,
                card.folder.id,
                card.createdBy,
                card.modifiedBy))
            .from(card)
            .orderBy(card.id.desc())
            .where(card.folder.member.id.eq(memberId)
                .and(cursorId(lastIdx))
                .and(card.isDeleted.eq(false)))
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return makePage(cardDtoList, pageable);
    }

    public Page<CardDto> findAllCardByFolderId(Long lastIdx, Long folderId, Pageable pageable) {
        List<CardDto> cardDtoList = jpaQueryFactory.select(Projections.constructor(CardDto.class,
                card.id,
                card.link,
                card.title,
                card.content,
                card.isPublic,
                card.isDeleted,
                card.folder.id,
                card.createdBy,
                card.modifiedBy))
            .from(card)
            .orderBy(card.id.desc())
            .where(card.folder.id.eq(folderId).and(cursorId(lastIdx))
                .and(card.isDeleted.eq(false)))
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return makePage(cardDtoList, pageable);
    }
}
