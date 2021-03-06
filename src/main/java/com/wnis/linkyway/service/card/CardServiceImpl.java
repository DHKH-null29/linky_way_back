package com.wnis.linkyway.service.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.wnis.linkyway.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.CopyCardsRequest;
import com.wnis.linkyway.dto.card.CopyPackageCardsRequest;
import com.wnis.linkyway.dto.card.SocialCardResponse;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Tag;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final TagRepository tagRepository;

    private final CardTagRepository cardTagRepository;

    private final FolderRepository folderRepository;
    
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public AddCardResponse addCard(Long memberId, CardRequest cardRequest) {
        Folder folder = folderRepository.findByIdAndMemberId(memberId, cardRequest.getFolderId())
                                        .orElseThrow(() -> new NotFoundEntityException(
                                                "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요."));

        Set<Long> tagIdList = cardRequest.getTagIdSet();
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        if (tagIdList.size() != tagList.size()) {
            throw new NotFoundEntityException("존재하지 않는 태그는 사용할 수 없습니다. 태그를 먼저 추가해주세요.");
        }

        Card savedCard = cardRepository.save(cardRequest.toEntity(folder));
        
        addCardTag(savedCard, tagList);

        return AddCardResponse.builder()
                              .cardId(savedCard.getId())
                              .build();
    }

    @Override
    @Transactional
    public CardResponse findCardByCardId(Long cardId, Long memberId) {
        Card card = cardRepository.findByCardIdAndMemberId(cardId, memberId)
                                  .orElseThrow(() -> new NotFoundEntityException("다른 회원 또는 존재하지 않는 카드를 조회 할 수 없습니다"));
        
        checkDeletedCardOne(card);
        
        List<CardTag> cardTagList = card.getCardTags();
        List<TagResponse> tagResponseList = new ArrayList<>();
        for (CardTag cardTag : cardTagList) {
            Tag tag = cardTag.getTag();
            tagResponseList.add(TagResponse.builder()
                                           .tagId(tag.getId())
                                           .tagName(tag.getName())
                                           .isPublic(tag.getIsPublic())
                                           .build());
        }

        return CardResponse.builder()
                           .cardId(card.getId())
                           .link(card.getLink())
                           .title(card.getTitle())
                           .content(card.getContent())
                           .folderId(card.getFolder()
                                         .getId())
                           .isPublic(card.getIsPublic())
                           .tags(tagResponseList)
                           .build();
    }

    @Override
    @Transactional
    public Long updateCard(Long memberId, Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findByCardIdAndMemberId(cardId, memberId)
                                  .orElseThrow(() -> new ResourceConflictException("해당 회원만 카드 수정 가능합니다"));
    
        checkDeletedCardOne(card);
        
        Folder oldFolder = card.getFolder();
        if (cardRequest.getFolderId() != oldFolder.getId()) {
            Folder folder = folderRepository.findByIdAndMemberId(oldFolder.getMember()
                                                                          .getId(),
                                                                 cardRequest.getFolderId())
                                            .orElseThrow(() -> new NotFoundEntityException(
                                                    "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요."));
            card.updateFolder(folder);
        }

        card.updateLink(cardRequest.getLink());
        card.updateTitle(cardRequest.getTitle());
        card.updateContent(cardRequest.getContent());
        card.updateIsPublic(cardRequest.getIsPublic());

        updateCardTagByCard(memberId, card, cardRequest);

        return card.getId();
    }

    private void updateCardTagByCard(Long memberId, Card savedCard, CardRequest newCard) {
        List<CardTag> oldCardTagList = savedCard.getCardTags();
        Set<Long> newTagIdList = newCard.getTagIdSet();
        
        List<CardTag> delCardTagList = new ArrayList<>();
        for (CardTag oldCardTag : oldCardTagList) {
            Long tagId = oldCardTag.getTag()
                                   .getId();
            // 기존 태그가 선택되지 않음 -> 삭제
            if (!newTagIdList.contains(tagId)) {
                delCardTagList.add(oldCardTag);
            } else {    // 새로운 태그가 선택됨 -> CardTag 추가
                newTagIdList.remove(tagId);
            }
        }
        cardTagRepository.deleteAll(delCardTagList);
        
        List<Tag> newTagList = tagRepository.findAllById(newTagIdList);
        addCardTag(savedCard, newTagList);
    }
    
    private void addCardTag(Card card, List<Tag> tagList) {
        List<CardTag> CardTagList = new ArrayList<>();
        for (Tag tag : tagList) {
            CardTag cardTag = CardTag.builder()
                                     .card(card)
                                     .tag(tag)
                                     .build();
            CardTagList.add(cardTag);
        }
        cardTagRepository.saveAll(CardTagList);
    }

    @Override
    @Transactional
    public Long deleteCard(Long cardId, Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("회원이 존재하지 않습니다");
        }
    
        Card card = cardRepository.findByCardIdAndMemberId(cardId, memberId)
                                  .orElseThrow(() -> new ResourceConflictException("회원 카드만 삭제가 가능합니다"));
        
        card.updateIsDeleted(true);
        return card.getId();
    }

    @Override
    public List<CardResponse> SearchCardByKeywordPersonalPage(String keyword, Long memberId, Pageable pageable) {
        List<Card> cardsList = cardRepository.findAllCardByKeyword(keyword, memberId, pageable);
        
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
    public List<CardResponse> findCardsByTagId(Long memberId, Long tagId, Pageable pageable) {
        tagRepository.findByIdAndMemberId(memberId, tagId)
                     .orElseThrow(() -> new ResourceConflictException("존재하지 않는 태그입니다. 태그를 확인해주세요."));

        List<Card> cardList = cardRepository.findCardsByTagId(tagId, pageable);
        
        
        return toResponseList(cardList);
    }

    @Override
    @Transactional
    public List<SocialCardResponse> findIsPublicCardsByTagId(Long tagId, Pageable pageable) {
        Tag tag = tagRepository.findById(tagId)
                               .orElseThrow(() -> new ResourceConflictException("존재하지 않는 태그입니다. 태그를 확인해주세요."));
        if (!tag.getIsPublic()) {
            throw new NotAccessableException("소셜 공유가 허용되지 않은 태그입니다.");
        }

        List<Card> cardList = cardRepository.findIsPublicCardsByTagId(tagId, pageable);
        
        return toResponseList(cardList).stream()
                                       .map((cardResponse) -> new SocialCardResponse(cardResponse))
                                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByFolderId(Long memberId, Long folderId, boolean findDeep, Pageable pageable) {
        Folder folder = folderRepository.findByIdAndMemberId(memberId, folderId)
                                        .orElseThrow(() -> new ResourceConflictException("존재하지 않는 폴더입니다. 폴더를 확인해주세요."));
    
        List<Card> cardList;
        if (!findDeep) {
            cardList = cardRepository.findCardsByFolderId(folderId, pageable);
        } else {
            List<Long> folderList = findAllFolderId(folder);
            cardList = cardRepository.findAllInFolderIds(folderList, pageable);
        }
        return toResponseList(cardList);
    }
    
    private List<Long> findAllFolderId(Folder folder) {
        List<Long> folderList = new ArrayList<>();
        findAllFolderIdRecursive(folder, folderList);
        return folderList;
    }
    private void findAllFolderIdRecursive(Folder folder, List<Long> folderList) {
        folderList.add(folder.getId());
    
        for (Folder f : folder.getChildren()) {
            findAllFolderIdRecursive(f, folderList);
        }
    }

    @Override
    @Transactional
    public List<CardResponse> findCardsByMemberId(Long memberId, Pageable pageable) {
        List<Card> cardList = cardRepository.findCardsByMemberId(memberId, pageable);
        
        return toResponseList(cardList);
    }

    private List<CardResponse> toResponseList(List<Card> cardList) {
        List<CardResponse> cardResponseList = new ArrayList<>();
        for (Card card : cardList) {
            List<Tag> tagList = card.getCardTags()
                                    .stream()
                                    .map(CardTag::getTag)
                                    .collect(Collectors.toList());

            List<TagResponse> tagResponseList = tagList.stream()
                                                       .map((tag) -> new TagResponse(tag))
                                                       .collect(Collectors.toList());

            cardResponseList.add(CardResponse.builder()
                                             .cardId(card.getId())
                                             .title(card.getTitle())
                                             .content(card.getContent())
                                             .link(card.getLink())
                                             .tags(tagResponseList)
                                             .folderId(card.getFolder()
                                                           .getId())
                                             .isPublic(card.getIsPublic())
                                             .build());
        }
        return cardResponseList;
    }

    @Override
    @Transactional
    public int copyCardsInPackage(CopyPackageCardsRequest copyPackageCardsRequest) {
        Folder folder = folderRepository.findById(copyPackageCardsRequest.getFolderId())
                                        .orElseThrow(() -> new NotFoundEntityException(
                                                "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요."));
        Tag tag = tagRepository.findById(copyPackageCardsRequest.getTagId())
                               .orElseThrow(() -> new ResourceConflictException("존재하지 않는 태그입니다. 태그를 확인해주세요."));

        List<CopyCardsRequest> cardRequestList = copyPackageCardsRequest.getCopyCardsRequestList();
        List<Card> cardList = new ArrayList<>();
        for (CopyCardsRequest card : cardRequestList) {
            cardList.add(card.toEntity(folder, copyPackageCardsRequest.isPublic()));
        }

        List<Card> savedCardList = cardRepository.saveAll(cardList);

        List<CardTag> cardTagList = new ArrayList<>();
        for (Card savedCard : savedCardList) {
            cardTagList.add(CardTag.builder()
                                   .card(savedCard)
                                   .tag(tag)
                                   .build());
        }
        cardTagRepository.saveAll(cardTagList);

        return savedCardList.size();
    }
    
    private void checkDeletedCardOne(Card card) {
        if (card.getIsDeleted()) {
            throw new ResourceNotFoundException("해당 카드를 수정 할 수 없습니다");
        }
    }
}