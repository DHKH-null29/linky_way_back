package com.wnis.linkyway.dto.folder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wnis.linkyway.entity.Folder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FolderResponse {

    Long folderId;
    Long parentId;
    Long level;
    String name;

    List<FolderResponse> childFolderList = new ArrayList<>();

    @Builder
    private FolderResponse(Long folderId, Long parentId, Long level, String name) {
        this.folderId = folderId;
        this.parentId = parentId;
        this.level = level;
        this.name = name;
    }

    public FolderResponse(Folder folder) {
        this.folderId = folder.getId();
        if (folder.getParent() == null) {
            this.parentId = null;
        } else {
            this.parentId = folder.getParent()
                                  .getId();
        }
        this.level = folder.getDepth();
        this.name = folder.getName();
        this.childFolderList = makeDirectoryTree(folder);
    }

    private FolderResponse makeFolderResponse(Folder folder) {
        return FolderResponse.builder()
                             .folderId(folder.getId())
                             .parentId(folder.getParent()
                                             .getId())
                             .level(folder.getDepth())
                             .name(folder.getName())
                             .build();
    }

    private List<FolderResponse> makeDirectoryTree(Folder folder) {
        Queue<Folder> queue1 = new ArrayDeque<>();
        Queue<FolderResponse> queue2 = new ArrayDeque<>();
        folder.getChildren()
              .forEach((f) -> {
                  queue1.add(f);
                  FolderResponse newFolderResponse = makeFolderResponse(f);
                  this.getChildFolderList()
                      .add(newFolderResponse);
                  queue2.add(newFolderResponse);
              });

        while (!queue1.isEmpty()) {
            Folder currentFolder = queue1.poll();
            FolderResponse currentFolderResponse = queue2.poll();

            for (Folder f : currentFolder.getChildren()) {
                FolderResponse newFolderResponse = makeFolderResponse(f);
                currentFolderResponse.getChildFolderList()
                                     .add(newFolderResponse);
                queue1.add(f);
                queue2.add(newFolderResponse);
            }
        }
        return this.childFolderList;
    }

}
