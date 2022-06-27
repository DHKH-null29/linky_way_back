package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.service.PackageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;
    @GetMapping("/social")
    @ApiOperation(value = "소셜 태그 기반 검색", notes = "태그를 기반으로 검색한다. isLike 속성에 따라 정확히 혹은 Like 검색 수행")
    ResponseEntity<Response> searchSocialByTagName(@RequestParam(value = "tagName", required = false) String tagName,
            @RequestParam(value = "isLike", required = false) boolean isLike,
            Pageable pageable) {
        List<PackageResponse> allPackageByTagName = packageService.findAllPackageByTagName(tagName,isLike, pageable);

        return ResponseEntity.ok(Response.of(HttpStatus.OK, allPackageByTagName, "개인 검색 성공"));
    }
    
}
