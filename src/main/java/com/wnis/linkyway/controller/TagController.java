package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.tag.TagService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags/table")
    @ApiOperation(value = "태그 조회", notes = "해당 회원이 가지고 있는 태그 리스트를 조회한다.")
    @Authenticated
    public ResponseEntity<Response> searchTags(@CurrentMember Long memberId) {
        List<TagResponse> response = tagService.searchTags(memberId);
        return ResponseEntity.status(200)
                             .body(Response.of(HttpStatus.OK, response, "성공적으로 태그를 조회했습니다"));
    }

    @PostMapping("/tags")
    @ApiOperation(value = "태그 추가", notes = "해당 회원이 가지고 있는 태그를 추가한다.")
    @Authenticated
    public ResponseEntity<Response> addTag(@ApiIgnore @CurrentMember Long memberId,
            @Validated(ValidationSequence.class) @RequestBody TagRequest tagRequest) {

        TagResponse response = tagService.addTag(tagRequest, memberId);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(Response.of(HttpStatus.OK, response, "태그가 성공적으로 생성되었습니다"));
    }

    @PutMapping("/tags/{tagId}")
    @ApiOperation(value = "태그 수정", notes = "해당 회원이 가지고 있는 태그를 수정한다.")
    @Authenticated
    public ResponseEntity<Response> setTag(@ApiIgnore @CurrentMember Long memberId,
            @PathVariable(value = "tagId") Long tagId,
            @RequestBody TagRequest tagRequest) {

        TagResponse response = tagService.setTag(tagRequest, tagId);
        return ResponseEntity.status(200)
                             .body(Response.of(HttpStatus.OK, response, "태그가 성공적으로 수정되었습니다"));
    }

    @DeleteMapping("/tags/{tagId}")
    @ApiOperation(value = "태그 삭제", notes = "해당 회원이 가지오 있는 태그를 삭제한다.")
    @Authenticated
    public ResponseEntity<Response> deleteTag(@PathVariable(value = "tagId") Long tagId) {

        TagResponse response = tagService.deleteTag(tagId);
        return ResponseEntity.status(200)
                             .body(Response.of(HttpStatus.OK, response, "태그를 성공적으로 삭제했습니다"));
    }
}
