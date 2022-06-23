package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService {
    
    private final CardTagRepository cardTagRepository;
    private final TagRepository tagRepository;
    
    public List<PackageResponse> findAllPackageByTagName(String tagName, Pageable pageable) {
        
        List<Tag> tagList = tagRepository.findAllDistinctTagListByTagName(tagName, pageable);
        List<PackageResponse> packageResponseList = new ArrayList<>();
        for (Tag t : tagList) {
            long numberOfCard = cardTagRepository.countByTagId(t.getId());
            PackageResponse packageResponse = PackageResponse.builder()
                    .tagId(t.getId())
                    .tagName(t.getName())
                    .memberId(t.getMember().getId())
                    .nickname(t.getMember().getNickname())
                    .numberOfCard(numberOfCard)
                    .build();
            packageResponseList.add(packageResponse);
        }
        return packageResponseList;
    }
    
    
    
}
