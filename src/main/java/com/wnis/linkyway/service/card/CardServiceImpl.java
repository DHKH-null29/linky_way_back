package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardDto;
import com.wnis.linkyway.dto.card.io.AddCardResponse;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.card.io.CopyCardsRequest;
import com.wnis.linkyway.dto.card.io.CopyPackageCardsRequest;
import com.wnis.linkyway.dto.cardtag.CardTagDto;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.mapper.CardMapper;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.repository.card.CardRepositoryCustom;
import com.wnis.linkyway.repository.cardtag.CardTagRepository;
import com.wnis.linkyway.repository.cardtag.CardTagRepositoryCustom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final CardRepositoryCustom cardRepositoryCustom;

    private final TagRepository tagRepository;

    private final CardTagRepository cardTagRepository;

    private final FolderRepository folderRepository;

    private final MemberRepository memberRepository;

    private final CardTagRepositoryCustom cardTagRepositoryCustom;

    @Override
    @Transactional
    public AddCardResponse addCard(Long memberId, CardRequest cardRequest) {
        Folder folder = folderRepository.findByIdAndMemberId(memberId, cardRequest.getFolderId())
            .orElseThrow(() -> new NotFoundEntityException(
                "해당 폴더가 존재하지 않습니다. 폴더를 먼저 생성해주세요"));

        Set<Long> tagIdList = cardRequest.getTagIdSet();
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        if (tagIdList.size() != tagList.size()) {
            throw new NotFoundEntityException("존재하지 않는 태그는 사용할 수 없습니다. 태그를 먼저 추가해주세요");
        }

        Card savedCard = cardRepository.save(cardRequest.toEntity(folder));

        addCardTagConnection(savedCard, tagList);

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

        List<TagResponse> cardTagList = cardTagRepository.findAllTagResponseByCardId(cardId);

        return CardMapper.toCardResponse(card, cardTagList);
    }

    @Override
    @Transactional
    public Long updateCard(Long memberId, Long cardId, CardRequest cardRequest) {
        Card card = cardRepository.findByCardIdAndMemberId(cardId, memberId)
            .orElseThrow(() -> new ResourceConflictException("해당 회원만 카드 수정 가능합니다"));

        checkDeletedCardOne(card);

        Folder oldFolder = card.getFolder();
        if (!Objects.equals(cardRequest.getFolderId(), oldFolder.getId())) {
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

        updateCardTag(card, cardRequest);

        return card.getId();
    }

    private void updateCardTag(Card oldCard, CardRequest newCard) {
        List<TagResponse> oldTagList = cardTagRepository.findAllTagResponseByCardId(
            oldCard.getId());

        Set<Long> oldTagSet = oldTagList.stream().map(TagResponse::getTagId)
            .collect(Collectors.toSet());
        Set<Long> newTagIdSet = newCard.getTagIdSet();

        // 기존 태그 리스트 기준으로 생성할 카드 태그 관계들 -> 추가
        Set<Long> addTagIdSet = new HashSet<>(newTagIdSet);
        addTagIdSet.removeAll(oldTagSet);
        List<Tag> newTagList = tagRepository.findAllById(newTagIdSet);
        addCardTagConnection(oldCard, newTagList);

        // 기존 태그 리스트 기준으로 없어질 카드 태그 관계들 -> 삭제
        Set<Long> deleteTagIdSet = new HashSet<>(oldTagSet);
        deleteTagIdSet.removeAll(addTagIdSet);

        List<Long> deleteCardTagIdList = cardTagRepository.findAllCardTagIdInTagSet(
            deleteTagIdSet);
        cardTagRepository.deleteAllById(deleteCardTagIdList);

    }

    private void addCardTagConnection(Card card, List<Tag> tagList) {
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

    // 커서 페이징
    @Override
    public List<CardResponse> SearchCardByKeywordPersonalPage(Long lastIdx, String keyword,
        Long memberId,
        Pageable pageable) {
        List<CardDto> cardDtoList = cardRepositoryCustom.findAllCardContainKeyword(
            lastIdx, keyword, memberId, pageable);

        List<CardResponse> cardResponseList = new ArrayList<>();
        for (CardDto card : cardDtoList) {
            List<TagResponse> tags = cardTagRepository.findAllTagResponseByCardId(card.getId());
            CardResponse cardResponse = CardMapper.toCardResponse(card, tags);
            cardResponseList.add(cardResponse);
        }

        return cardResponseList;
    }

    // 커서 페이징
    @Override
    @Transactional
    public List<CardResponse> findCardsByTagId(Long lastIdx, Long memberId, Long tagId,
        Pageable pageable) {
        tagRepository.findByIdAndMemberId(memberId, tagId)
            .orElseThrow(() -> new ResourceConflictException("존재하지 않는 태그입니다. 태그를 확인해주세요."));
        List<CardDto> cardDtoList = cardRepositoryCustom.findAllCardByTadId(lastIdx, tagId,
            pageable);
        return toResponseList(cardDtoList, CardDto.class);
    }


    @Override
    @Transactional
    public List<CardResponse> findCardsByFolderId(Long lastIdx, Long memberId, Long folderId,
        boolean findDeep,
        Pageable pageable) {
        Folder folder = folderRepository.findByIdAndMemberId(memberId, folderId)
            .orElseThrow(() -> new ResourceConflictException("존재하지 않는 폴더입니다. 폴더를 확인해주세요."));

        List<CardDto> cardDtoList;
        if (!findDeep) {
            cardDtoList = cardRepositoryCustom.findAllCardByFolderId(lastIdx, folderId, pageable);
        } else {
            List<Long> folderList = findAllFolderId(folder);
            cardDtoList = cardRepositoryCustom.findAllCardByFolderIds(lastIdx, folderList,
                pageable);
        }
        return toResponseList(cardDtoList, CardDto.class);
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
    public List<CardResponse> findCardsByMemberId(Long lastIdx, Long memberId, Pageable pageable) {
        List<CardDto> cardDtoList = cardRepositoryCustom.findAllCardByMemberId(lastIdx, memberId,
            pageable);
        return toResponseList(cardDtoList, CardDto.class);
    }

    private <T> List<CardResponse> toResponseList(List<T> cardList, Class<T> tClass) {
        List<CardTagDto> cardTagDtoList;
        if (tClass.equals(Card.class)) {
            cardTagDtoList = cardTagRepositoryCustom.findCardTagByCard(
                (Collection<Card>) cardList);
        } else if (tClass.equals(CardDto.class)) {
            List<Long> cardIdList = cardList.stream().map((T t) -> ((CardDto) t).getId())
                .collect(Collectors.toList());
            cardTagDtoList = cardTagRepositoryCustom.findCardTagByCardId(cardIdList);
        } else {
            throw new RuntimeException("Card, CardDto 형식만 입력할 수 있습니다");
        }

        Map<Long, List<CardTagDto>> map = cardTagDtoList.stream()
            .collect(Collectors.groupingBy(CardTagDto::getCardId));

        return CardMapper.toCardResponse(cardList, map, tClass);
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