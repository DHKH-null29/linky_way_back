package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageService {
    
    private final CardTagRepository cardTagRepository;
    
    private final TagRepository tagRepository;
    
    public List<PackageResponse> findAllPackageByTagName(String tagName, boolean isLike, Pageable pageable) {
        if (tagName == null) {
            tagName = "";
        }
        
        List<Tag> tagList = findALlTagList(isLike, tagName, pageable)
                .stream().filter(tag -> tag.getIsPublic()).collect(Collectors.toList());
        
        List<PackageResponse> packageResponseList = new ArrayList<>();
        for (Tag t : tagList) {
            List<Card> cardList = cardTagRepository.findAllPublicCardByTagId(t.getId())
                    .stream().filter(card -> !card.getIsDeleted()).collect(Collectors.toList());
            
            PackageResponse packageResponse = PackageResponse.builder()
                                                             .tagId(t.getId())
                                                             .tagName(t.getName())
                                                             .memberId(t.getMember().getId())
                                                             .nickname(t.getMember().getNickname())
                                                             .numberOfCard(cardList.size())
                                                             .build();
            // 패키지에 존재하는 카드가 있어야 response 등록
            if (packageResponse.getNumberOfCard() > 0) {
                packageResponseList.add(packageResponse);
            }
        }
        return packageResponseList;
    }
    
    private List<Tag> findALlTagList(boolean isLike, String tagName, Pageable pageable) {
        if (isLike) {
            return tagRepository.findAllDistinctTagListLikeTagName(tagName, pageable);
        }
        return tagRepository.findAllDistinctTagListByTagName(tagName, pageable);
    }
}
