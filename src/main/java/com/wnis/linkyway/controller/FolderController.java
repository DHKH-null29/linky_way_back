package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.folder.FolderService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FolderController {
    
    private final FolderService folderService;
    
    @GetMapping("/folders/{folderId}")
    @ApiOperation(value = "폴더 조회", notes = "해당 회원이 가지고 있는 폴더를 ID를 통해 조회한다.")
    @Authenticated
    public ResponseEntity<Response> searchFolder(@PathVariable(value = "folderId") Long folderId) {
        Response<FolderResponse> folderResponse = folderService.findFolder(folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PostMapping("/folders/super") // 삭제 예정
    @Authenticated
    public ResponseEntity<Response> addSuperFolder(
            @CurrentMember Long memberId) {
        
        Response<FolderResponse> folderResponse = folderService.addSuperFolder(memberId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PostMapping("/folders")
    @ApiOperation(value = "폴더 추가", notes = "회원이 지정한 폴더에 하위 폴더를 추가한다.")
    @Authenticated
    public ResponseEntity<Response> addFolder(
            @Validated(ValidationSequence.class) @RequestBody AddFolderRequest addFolderRequest,
            @CurrentMember Long memberId) {
        
        Response<FolderResponse> folderResponse = folderService.addFolder(addFolderRequest, memberId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PutMapping("/folders/{folderId}/name")
    @ApiOperation(value = "폴더 이름 수정", notes = "해당 폴더의 이름을 수정한다.")
    @Authenticated
    public ResponseEntity<Response> setFolderName(
            @Validated(ValidationSequence.class) @RequestBody SetFolderNameRequest setFolderNameRequest,
            @PathVariable Long folderId) {
        
        Response<FolderResponse> folderResponse = folderService.setFolderName(setFolderNameRequest, folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PutMapping("/folders/{folderId}/path")
    @ApiOperation(value = "폴더 경로 수정", notes = "해당 폴더의 경로를 수정한다.")
    @Authenticated
    public ResponseEntity<Response> setFolderPath(
            @Validated(ValidationSequence.class) @RequestBody SetFolderPathRequest setFolderPathRequest,
            @PathVariable(value = "folderId") Long folderId) {
        
        Response<FolderResponse> folderResponse = folderService.setFolderPath(setFolderPathRequest, folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @DeleteMapping("/folders/{folderId}")
    @ApiOperation(value = "폴더 삭제", notes = "해당 폴더를 삭제한다")
    @Authenticated
    public ResponseEntity<Response> deleteFolder(@PathVariable(value = "folderId") Long folderId) {
        Response<FolderResponse> folderResponse = folderService.deleteFolder(folderId);
        return ResponseEntity.ok(folderResponse);
    }
}
