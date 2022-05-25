package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.tag.TagService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TagController {
    
    private final TagService tagService;
    
    @GetMapping("/tags/table")
    @ApiOperation(value = "태그 조회", notes = "해당 회원이 가지고 있는 태그 리스트를 조회한다.")
    @Authenticated
    public ResponseEntity<Response> searchTags(@CurrentMember Long memberId) {
        Response response = tagService.searchTags(memberId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/tags")
    @ApiOperation(value = "태그 추가", notes = "해당 회원이 가지고 있는 태그를 추가한다.")
    @Authenticated
    public ResponseEntity<Response> addTag(
            @ApiIgnore @CurrentMember Long memberId,
            @Validated(ValidationSequence.class) @RequestBody TagRequest tagRequest) {
        
        Response response = tagService.addTag(tagRequest, memberId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/tags/{tagId}")
    @ApiOperation(value = "태그 수정", notes = "해당 회원이 가지고 있는 태그를 수정한다.")
    @Authenticated
    public ResponseEntity<Response> setTag(
            @ApiIgnore @CurrentMember Long memberId,
            @PathVariable(value = "tagId") Long tagId,
            @RequestBody TagRequest tagRequest) {
        
        Response response = tagService.setTag(tagRequest, tagId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/tags/{tagId}")
    @ApiOperation(value = "태그 삭제", notes = "해당 회원이 가지오 있는 태그를 삭제한다.")
    @Authenticated
    public ResponseEntity<Response> deleteTag(
            @PathVariable(value = "tagId") Long tagId) {
        
        Response response = tagService.deleteTag(tagId);
        return ResponseEntity.ok(response);
    }
}
