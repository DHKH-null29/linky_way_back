package com.wnis.linkyway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "card_tag")
public class CardTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_tag_id", nullable = false)
    private Long id;

    ////********************연관 관게  ***************************/////

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_card_id")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_tag_id")
    private Tag tag;


    @Builder
    public CardTag(Card card, Tag tag) {
        this.card = card;
        if (card != null)
            card.getCardTags().add(this);
        this.tag = tag;
        if (tag != null)
            tag.getCardTags().add(this);
    }
}