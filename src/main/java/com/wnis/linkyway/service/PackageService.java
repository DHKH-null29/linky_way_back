package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.PackageDto;
import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.repository.CardTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService {
    
    private final CardTagRepository cardTagRepository;
    
    public List<PackageResponse> findAllPackageByTagName(String tagName) {
        
        List<PackageDto> packageDtoList = cardTagRepository.findAllPackageDtoByTagName(tagName);
        Map<Long, Map<Long, List<PackageDto>>> map = packageDtoList.stream()
                                                                   .collect(Collectors.groupingBy(
                                                                               PackageDto::getMemberId,
                                                                               Collectors.groupingBy(
                                                                                       PackageDto::getTagId)));
        
        
        
        return manufacturePackageDtoMapToPackageResponse(map);
    }
    
    
    private List<PackageResponse>  manufacturePackageDtoMapToPackageResponse(Map<Long, Map<Long, List<PackageDto>>> map) {
        List<PackageResponse> response = new ArrayList<>();
    
        for (Map.Entry<Long, Map<Long, List<PackageDto>>> firstEntry : map.entrySet()) {
            for (Map.Entry<Long, List<PackageDto>> secondEntry : firstEntry.getValue().entrySet()) {
                List<PackageDto> items = secondEntry.getValue();
                PackageResponse packageResponse = PackageResponse.builder()
                                                                 .memberId(firstEntry.getKey())
                                                                 .nickname(items.get(0).getNickname())
                                                                 .tagId(secondEntry.getKey())
                                                                 .tagName(items.get(0).getTagName())
                                                                 .numberOfCard(items.size())
                                                                 .build();
                response.add(packageResponse);
            }
        }
        return response;
    }
}
