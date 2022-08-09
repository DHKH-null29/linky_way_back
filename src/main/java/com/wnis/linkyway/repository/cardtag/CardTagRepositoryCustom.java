package com.wnis.linkyway.repository.cardtag;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wnis.linkyway.dto.card.CardDto;
import com.wnis.linkyway.dto.cardtag.CardTagDto;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.QCard;
import com.wnis.linkyway.entity.QCardTag;
import com.wnis.linkyway.entity.QTag;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CardTagRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCardTag cardTag = QCardTag.cardTag;
    private final QCard card = QCard.card;
    private final QTag tag = QTag.tag;

    public List<CardTagDto> findCardTagByCardId(Collection<Long> ids) {
        return jpaQueryFactory.select(
                Projections.constructor(CardTagDto.class,
                    cardTag.id,
                    card.id,
                    card.link,
                    card.title,
                    card.content,
                    card.isPublic,
                    card.folder.id,
                    card.isDeleted,
                    tag.id,
                    tag.name
                )
            ).from(cardTag)
            .join(cardTag.card, card)
            .join(cardTag.tag, tag)
            .where(card.id.in(ids))
            .fetch();
    }

    public List<CardTagDto> findCardTagByCard(Collection<Card> cards) {
        return jpaQueryFactory.select(
                Projections.constructor(CardTagDto.class,
                    cardTag.id,
                    card.id,
                    card.link,
                    card.title,
                    card.content,
                    card.isPublic,
                    card.folder.id,
                    card.isDeleted,
                    tag.id,
                    tag.name
                )
            ).from(cardTag)
            .join(cardTag.card, card)
            .join(cardTag.tag, tag)
            .where(card.in(cards))
            .fetch();
    }

}
