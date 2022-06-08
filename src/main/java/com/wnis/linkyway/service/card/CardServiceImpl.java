package com.wnis.linkyway.service.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final TagRepository tagRepository;

    private final CardTagRepository cardTagRepository;

    private final FolderRepository folderRepository;

    @Override
    @Transactional
    public AddCardResponse addCard(Long memberId, CardRequest cardRequest) {
        Folder folder = folderRepository.findByIdAndMemberId(memberId, cardRequest.getFolderId())
                                        .orElseThrow(() -> new NotFoundEntityException(
                                                "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요."));
        Card savedCard = cardRepository.save(cardRequest.toEntity(folder));

        addCardTagByCard(memberId, savedCard, cardRequest.getTagIdSet());

        return AddCardResponse.builder().cardId(savedCard.getId()).build();
    }

    @Override
    @Transactional
    public CardResponse findCardByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(
                                          () -> new NotFoundEntityException("해당 카드가 존재하지 않습니다"));
        List<CardTag> cardTagList = card.getCardTags();
        List<Tag> tagList = new ArrayList<Tag>();
        for (CardTag cardTag : cardTagList) {
            tagList.add(cardTag.getTag());
        }

        return CardResponse.builder()
                           .cardId(card.getId())
                           .link(card.getLink())
                           .title(card.getTitle())
                           .content(card.getContent())
                           .shareable(card.getShareable())
                           .tags(tagList)
                           .build();
    }

    @Override
    @Transactional
    public Card updateCard(Long memberId, Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(() -> new NotModifyEmptyEntityException(
                                          "해당 카드가 존재하지 않아 수정이 불가능합니다"));
        Folder oldFolder = card.getFolder();
        if (cardRequest.getFolderId() != oldFolder.getId()) {
            Folder folder = folderRepository.findByIdAndMemberId(oldFolder.getMember().getId(),
                    cardRequest.getFolderId())
                                            .orElseThrow(() -> new NotFoundEntityException(
                                                    "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요."));
            card.updateFolder(folder);
        }

        card.updateLink(cardRequest.getLink());
        card.updateTitle(cardRequest.getTitle());
        card.updateContent(cardRequest.getContent());
        card.updateShareable(cardRequest.getShareable());
        updateCardTagByCard(memberId, card, cardRequest);

        return card;
    }

    private void addCardTagByCard(Long memberId, Card savedCard, Set<Long> tagIdList) {
        for (Long tagId : tagIdList) {
            Tag tag = tagRepository.findByIdAndMemberId(memberId, tagId)
                                   .orElseThrow(() -> new NotFoundEntityException(
                                           "존재하지 않는 태그는 사용할 수 없습니다. 태그를 먼저 추가해주세요."));
            if (!cardTagRepository.findByCardAndTag(savedCard, tag).isPresent()) {
                cardTagRepository.save(CardTag.builder().card(savedCard).tag(tag).build());
            }
        }
    }

    private void updateCardTagByCard(Long memberId, Card savedCard, CardRequest newCard) {
        List<CardTag> oldCardTagList = savedCard.getCardTags();
        Set<Long> newTagIdList = newCard.getTagIdSet();

        // 새로운 태그가 선택됨 -> CardTag 추가
        addCardTagByCard(memberId, savedCard, newTagIdList);

        // 기존 태그가 선택되지 않음 -> 삭제
        for (CardTag oldCardTag : oldCardTagList) {
            if (!newTagIdList.contains(oldCardTag.getTag().getId())) {
                cardTagRepository.deleteById(oldCardTag.getId());
            }
        }
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.findById(cardId)
                      .orElseThrow(
                              () -> new NotDeleteEmptyEntityException("해당 카드가 존재하지 않아 삭제가 불가능합니다"));
        cardRepository.deleteById(cardId);
    }

    @Override
    public List<CardResponse> personalSearchCardByKeyword(String keyword, Long memberId) {
        List<Card> cardsList = cardRepository.findAllCardByKeyword(keyword, memberId);
        List<CardResponse> result = new ArrayList<>();

        for (Card card : cardsList) {
            List<Tag> tags = new ArrayList<>();
            
            card.getCardTags().forEach(t -> {
                Tag tag = t.getTag();
                if (tag != null) {
                    tags.add(tag);
                }
            });
            
            CardResponse cardResponse = CardResponse.builder()
                    .cardId(card.getId())
                    .link(card.getLink())
                    .content(card.getContent())
                    .shareable(true)
                    .title(card.getTitle())
                    .tags(tags)
                    .build();
            
            result.add(cardResponse);
            
        }
        return result;
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByTagId(Long memberId, Long tagId) {
        tagRepository.findByIdAndMemberId(memberId, tagId)
                     .orElseThrow(() -> new ResourceConflictException(
                             "존재하지 않는 태그는 사용할 수 없습니다. 태그를 먼저 추가해주세요."));

        List<Card> cardList = cardRepository.findCardsByTagId(tagId);
        if (cardList.isEmpty()) {
            throw new NotFoundEntityException("태그에 해당하는 카드가 존재하지 않습니다.");
        }
        return toEntityList(cardList);
    }

    public List<CardResponse> toEntityList(List<Card> cardList) {
        List<CardResponse> cardResponseList = new ArrayList<CardResponse>();
        for (Card card : cardList) {
            List<Tag> tag = card.getCardTags()
                                .stream()
                                .map(CardTag::getTag)
                                .collect(Collectors.toList());
            cardResponseList.add(CardResponse.builder()
                                             .cardId(card.getId())
                                             .title(card.getTitle())
                                             .content(card.getContent())
                                             .link(card.getLink())
                                             .tags(tag)
                                             .shareable(card.getShareable())
                                             .build());
        }
        return cardResponseList;
    }
}