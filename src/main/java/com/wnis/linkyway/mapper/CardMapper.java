package com.wnis.linkyway.mapper;


import com.wnis.linkyway.dto.card.CardDto;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.cardtag.CardTagDto;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardMapper {

    public static CardResponse toCardResponse(Card card, List<TagResponse> tags) {
        return CardResponse.builder()
            .cardId(card.getId())
            .link(card.getLink())
            .title(card.getTitle())
            .content(card.getContent())
            .folderId(card.getFolder().getId())
            .isPublic(card.getIsPublic())
            .tags(tags)
            .build();
    }

    public static CardResponse toCardResponse(CardDto card, List<TagResponse> tags) {
        return CardResponse.builder()
            .cardId(card.getId())
            .link(card.getLink())
            .title(card.getTitle())
            .content(card.getContent())
            .folderId(card.getFolderId())
            .isPublic(card.getIsPublic())
            .tags(tags)
            .build();
    }

    public static <T> List<CardResponse> toCardResponse(List<T> cardList,
        Map<Long, List<CardTagDto>> map, Class<T> tClass) {
        if (tClass == Card.class) {
            return toCardResponseFromCard((List<Card>) cardList, map);
        } else if (tClass == CardDto.class) {
            return toCardResponseFromCardDto((List<CardDto>) cardList, map);
        }
        throw new RuntimeException("이 메서드는 Card, CardDto 타입 외에 사용 할 수 없습니다");
    }

    private static List<CardResponse> toCardResponseFromCard(List<Card> cardList,
        Map<Long, List<CardTagDto>> map) {
        List<CardResponse> cardResponseList = new ArrayList<>();
        for (Card card : cardList) {
            List<TagResponse> tags = new ArrayList<>();
            Long id = card.getId();
            if (map.containsKey(id)) {
                for (CardTagDto dto : map.get(id)) {
                    TagResponse tagResponse = TagResponse.builder()
                        .tagId(dto.getTagId())
                        .tagName(dto.getTagName())
                        .build();
                    tags.add(tagResponse);
                }
            }

            CardResponse cardResponse = CardResponse.builder()
                .cardId(card.getId())
                .link(card.getLink())
                .title(card.getTitle())
                .content(card.getContent())
                .folderId(card.getFolder().getId())
                .isPublic(card.getIsPublic())
                .tags(tags)
                .build();

            cardResponseList.add(cardResponse);
        }
        return cardResponseList;
    }

    private static List<CardResponse> toCardResponseFromCardDto(List<CardDto> cardList,
        Map<Long, List<CardTagDto>> map) {
        List<CardResponse> cardResponseList = new ArrayList<>();
        for (CardDto card : cardList) {
            List<TagResponse> tags = new ArrayList<>();
            Long id = card.getId();
            if (map.containsKey(id)) {
                for (CardTagDto dto : map.get(id)) {
                    TagResponse tagResponse = TagResponse.builder()
                        .tagId(dto.getTagId())
                        .tagName(dto.getTagName())
                        .build();
                    tags.add(tagResponse);
                }
            }

            CardResponse cardResponse = CardResponse.builder()
                .cardId(card.getId())
                .link(card.getLink())
                .title(card.getTitle())
                .content(card.getContent())
                .folderId(card.getFolderId())
                .isPublic(card.getIsPublic())
                .tags(tags)
                .build();

            cardResponseList.add(cardResponse);
        }
        return cardResponseList;
    }

    public static Card toEntity(CardRequest cardRequest) {
        return null;
    }
}
