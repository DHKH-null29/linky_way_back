package com.wnis.linkyway.service.folder;


import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;

public interface FolderService {
    
    Response<FolderResponse> findFolderSuper(Long memberId);
    
    Response<FolderResponse> findFolder(Long folderId);
    
    Response<FolderResponse> addSuperFolder(Long memberId);
    
    Response<FolderResponse> addFolder(AddFolderRequest addFolderRequest, Long memberId);
    
    Response<FolderResponse> setFolderPath(SetFolderPathRequest setFolderPathRequest, Long folderId);
    
    Response<FolderResponse> setFolderName(SetFolderNameRequest setFolderNameRequest, Long folderId);
    
    Response<FolderResponse> deleteFolder(Long folderId);
}
