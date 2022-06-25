package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.service.PackageService;
import lombok.RequiredArgsConstructor;
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
    ResponseEntity<Response> searchSocialByTagName(@RequestParam(value = "tagName", required = false) String tagName) {
        List<PackageResponse> allPackageByTagName = packageService.findAllPackageByTagName(tagName);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, allPackageByTagName, "개인 검색 성공"));
    }
    
}
