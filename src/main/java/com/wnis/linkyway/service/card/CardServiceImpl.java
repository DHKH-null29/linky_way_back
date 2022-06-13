package com.wnis.linkyway.service.card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wnis.linkyway.dto.tag.TagResponse;
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
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final TagRepository tagRepository;

    private final CardTagRepository cardTagRepository;

    private final FolderRepository folderRepository;

    @Override
    @Transactional
    public AddCardResponse addCard(Long memberId, CardRequest cardRequest) {
        Boolean isExistFolder = folderRepository.existsById(cardRequest.getFolderId());
        if (!isExistFolder) {
            throw new NotFoundEntityException("해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요");
        }
        Folder folder = folderRepository.getById(cardRequest.getFolderId()); // 프록시 객체 외래키 적용 용도
        
        Card savedCard = cardRepository.save(cardRequest.toEntity(folder));
        addCardAndTagConnection(savedCard, cardRequest.getTagIdSet());

        return AddCardResponse.builder()
                              .cardId(savedCard.getId())
                              .build();
    }

    @Override
    @Transactional
    public CardResponse findCardByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(() -> new NotFoundEntityException("해당 카드가 존재하지 않습니다"));
        
        List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(cardId);
        
        return CardResponse.builder()
                           .cardId(card.getId())
                           .link(card.getLink())
                           .title(card.getTitle())
                           .content(card.getContent())
                           .isPublic(card.getIsPublic())
                           .isDeleted(card.getIsDeleted())
                           .tags(tags)
                           .build();
    }

    @Override
    @Transactional
    public Card updateCard(Long memberId, Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findById(cardId)
                                  .orElseThrow(() -> new NotModifyEmptyEntityException("해당 카드가 존재하지 않아 수정이 불가능합니다"));
        
        Long requestFolderId = cardRequest.getFolderId();
        if (requestFolderId != null) {
            Boolean isExistFolder = folderRepository.existsById(requestFolderId);
            if (!isExistFolder) {
                throw new NotFoundEntityException("해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요");
            }
            Folder folder = folderRepository.getById(requestFolderId); // folder 프록시 객체
            card.updateFolder(folder); // 프록시 객체를 넣으면 CARD 는 folderId를 외래키로 등록함
        }
        
        card.updateLink(cardRequest.getLink());
        card.updateTitle(cardRequest.getTitle());
        card.updateContent(cardRequest.getContent());
        card.updateIsPublic(cardRequest.getIsPublic());
        cardRepository.save(card);  // card and cardTag update
        
        Set<Long> beforeTagIdSet = cardTagRepository.findAllTagIdByCardId(cardId);
        Set<Long> afterTagIdSet = cardRequest.getTagIdSet();
        
        // before, after 교집합
        Set<Long> retainTagIdSet = new HashSet<>(afterTagIdSet);
        retainTagIdSet.retainAll(beforeTagIdSet);
        
        // after - before
        // 새로운 카드와 태그 관계 생성, 새로운 태그 생성 x
        afterTagIdSet.removeAll(retainTagIdSet);
        addCardAndTagConnection(card, afterTagIdSet);
        
        // before - after
        // 카드와 태그의 연관관계는 삭제, 기존 태그 삭제 x
        beforeTagIdSet.removeAll(retainTagIdSet);
        deleteCardAndTagConnection(beforeTagIdSet);
        
        return card;
    }

    private void addCardAndTagConnection(Card savedCard, Set<Long> tagIdList) {
        // 카드 태그 테이블는 서로 연결이 되야만 관리한다.
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        tagList.forEach((tag -> {
            CardTag cardTag = CardTag.builder()
                    .card(savedCard)
                    .tag(tag)
                    .build();
            cardTagRepository.save(cardTag);
        }));
        
        
    }

    private void deleteCardAndTagConnection(Set<Long> tagIdSet) {
        List<Long> ids = cardTagRepository.findAllCardTagIdInTagSet(tagIdSet);
        cardTagRepository.deleteAllCardTagInIds(ids);

    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.findById(cardId)
                      .orElseThrow(() -> new NotDeleteEmptyEntityException("해당 카드가 존재하지 않아 삭제가 불가능합니다"));
        cardRepository.deleteById(cardId);
    }

    @Override
    public List<CardResponse> SearchCardByKeywordpersonalPage(String keyword, Long memberId) {
        List<Card> cardsList = cardRepository.findAllCardByKeyword(keyword, memberId);
        List<CardResponse> cardResponseList = new ArrayList<>();
        for (Card card : cardsList) {
            List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(card.getId());
            CardResponse cardResponse = CardResponse.builder()
                    .cardId(card.getId())
                    .link(card.getLink())
                    .title(card.getTitle())
                    .content(card.getContent())
                    .isPublic(card.getIsPublic())
                    .tags(tags)
                    .build();
            cardResponseList.add(cardResponse);
        }
        
        return cardResponseList;
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByTagId(Long memberId, Long tagId) {
        Boolean isExistTag = tagRepository.existsById(tagId);
        if (!isExistTag) {
            throw new NotFoundEntityException("존재하지 않는 태그입니다. 태그를 확인해주세요");
        }

        List<Card> cardList = cardRepository.findCardsByTagId(tagId);
        return toEntityList(cardList);
    }

    @Override
    @Transactional
    public List<CardResponse> findShareableCardsByTagId(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                               .orElseThrow(() -> new ResourceConflictException("존재하지 않는 태그입니다. 태그를 확인해주세요."));
        if (!tag.getShareable()) {
            throw new NotAccessableException("소셜 공유가 허용되지 않은 태그입니다.");
        }

        List<Card> cardList = cardRepository.findShareableCardsByTagId(tagId);
        return toEntityList(cardList);
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByFolderId(Long memberId, Long folderId, boolean findDeep) {
        Boolean isExistFolder = folderRepository.existsById(folderId);
        if (!isExistFolder) {
            throw new NotFoundEntityException("존재하지 않는 폴더입니다. 폴더를 확인해주세요");
        }

        List<Card> cardList;
        if (!findDeep) {
            cardList = cardRepository.findCardsByFolderId(folderId);
        } else {
            cardList = cardRepository.findDeepFoldersCardsByFolderId(folderId);
        }
        
        return toEntityList(cardList);
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByMemberId(Long memberId) {
        List<Card> cardList = cardRepository.findCardsByMemberId(memberId);
        
        return toEntityList(cardList);
    }

    public List<CardResponse> toEntityList(List<Card> cardList) {
        List<CardResponse> cardResponseList = new ArrayList<>();
        for (Card card : cardList) {
            List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(card.getId());
            cardResponseList.add(CardResponse.builder()
                                             .cardId(card.getId())
                                             .title(card.getTitle())
                                             .content(card.getContent())
                                             .link(card.getLink())
                                             .tags(tags)
                                             .isPublic(card.getIsPublic())
                                             .build());
        }
        return cardResponseList;
    }
}