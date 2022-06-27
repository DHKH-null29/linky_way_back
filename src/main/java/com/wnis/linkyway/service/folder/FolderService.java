package com.wnis.linkyway.service.folder;

import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;

import java.util.List;

public interface FolderService {

    List<FolderResponse> findAllFolderSuper(Long memberId);

    FolderResponse findFolder(Long folderId, Long memberId);

    FolderResponse addFolder(AddFolderRequest addFolderRequest, Long memberId);

    FolderResponse updateFolderPath(UpdateFolderPathRequest setFolderPathRequest, Long folderId, Long memberId);

    FolderResponse updateFolderName(UpdateFolderNameRequest setFolderNameRequest, Long folderId, Long memberId);

    FolderResponse deleteFolder(Long folderId, Long memberId);
}
