package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.folder.FolderService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/super")
    @ApiOperation(value = "상위 폴더 조회", notes = "해당 회원의 상위 폴더를 조회한다")
    @Authenticated
    public ResponseEntity<Response<List<FolderResponse>>> searchFolderSuper(@CurrentMember Long memberId) {
        List<FolderResponse> response = folderService.findAllFolderSuper(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더 조회 성공"));
    }

    @GetMapping("/{folderId}")
    @ApiOperation(value = "폴더 조회", notes = "해당 회원이 가지고 있는 폴더를 ID를 통해 조회한다")
    @Authenticated
    public ResponseEntity<Response<FolderResponse>> searchFolder(@PathVariable(value = "folderId") Long folderId, @CurrentMember Long memberId) {
        FolderResponse response = folderService.findFolder(folderId, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더를 성공적으로 조회했습니다"));
    }

    @PostMapping
    @ApiOperation(value = "폴더 추가", notes = "회원이 지정한 폴더에 하위 폴더를 추가한다")
    @Authenticated
    public ResponseEntity<Response<FolderResponse>> addFolder(
            @Validated(ValidationSequence.class) @RequestBody AddFolderRequest addFolderRequest,
            @CurrentMember Long memberId) {

        FolderResponse response = folderService.addFolder(addFolderRequest, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더 생성 성공"));
    }

    @PutMapping("/{folderId}/name")
    @ApiOperation(value = "폴더 이름 수정", notes = "해당 폴더의 이름을 수정한다")
    @Authenticated
    public ResponseEntity<Response<FolderResponse>> updateFolderName(
            @Validated(ValidationSequence.class) @RequestBody UpdateFolderNameRequest updateFolderNameRequest,
            @PathVariable Long folderId, @CurrentMember Long memberId) {

        FolderResponse response = folderService.updateFolderName(updateFolderNameRequest, folderId, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더 이름이 성공적으로 수정되었습니다"));
    }

    @PutMapping("/{folderId}/path")
    @ApiOperation(value = "폴더 경로 수정", notes = "해당 폴더의 경로를 수정한다")
    @Authenticated
    public ResponseEntity<Response<FolderResponse>> updateFolderPath(
            @Validated(ValidationSequence.class) @RequestBody UpdateFolderPathRequest updateFolderPathRequest,
            @PathVariable(value = "folderId") Long folderId, @CurrentMember Long memberId) {

        FolderResponse response = folderService.updateFolderPath(updateFolderPathRequest, folderId, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더 경로가 성공적으로 수정되었습니다"));
    }

    @DeleteMapping("/{folderId}")
    @ApiOperation(value = "폴더 삭제", notes = "해당 폴더를 삭제한다")
    @Authenticated
    public ResponseEntity<Response<FolderResponse>> deleteFolder(@PathVariable(value = "folderId") Long folderId, @CurrentMember Long memberId) {
        FolderResponse response = folderService.deleteFolder(folderId, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "폴더와 하위 폴더가 성공적으로 삭제되었습니다"));
    }
}
