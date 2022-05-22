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
    @Authenticated
    ResponseEntity<Response> searchFolder(@PathVariable(value = "folderId") Long folderId) {
        Response<FolderResponse> folderResponse = folderService.findFolder(folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PostMapping("/folders/super")
    @Authenticated
    ResponseEntity<Response> addSuperFolder(
            @CurrentMember Long memberId) {
    
        Response<FolderResponse> folderResponse = folderService.addSuperFolder(memberId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PostMapping("/folders")
    @Authenticated
    ResponseEntity<Response> addFolder(
            @Validated(ValidationSequence.class) @RequestBody AddFolderRequest addFolderRequest,
            @CurrentMember Long memberId) {
        
        Response<FolderResponse> folderResponse = folderService.addFolder(addFolderRequest, memberId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PutMapping("/folders/{folderId}/name")
    @Authenticated
    ResponseEntity<Response> setFolderName(
            @Validated(ValidationSequence.class) @RequestBody SetFolderNameRequest setFolderNameRequest,
            @PathVariable Long folderId) {
        
        Response<FolderResponse> folderResponse = folderService.setFolderName(setFolderNameRequest, folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @PutMapping("/folders/{folderId}/path")
    @Authenticated
    ResponseEntity<Response> setFolderPath(
            @Validated(ValidationSequence.class) @RequestBody SetFolderPathRequest setFolderPathRequest,
            @PathVariable(value = "folderId") Long folderId) {
        
        Response<FolderResponse> folderResponse = folderService.setFolderPath(setFolderPathRequest, folderId);
        return ResponseEntity.ok(folderResponse);
    }
    
    @DeleteMapping("/folders/{folderId}")
    @Authenticated
    ResponseEntity<Response> deleteFolder(@PathVariable(value = "folderId") Long folderId) {
        Response<FolderResponse> folderResponse = folderService.deleteFolder(folderId);
        return ResponseEntity.ok(folderResponse);
    }
}
