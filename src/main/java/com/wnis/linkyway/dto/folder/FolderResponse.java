package com.wnis.linkyway.dto.folder;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FolderResponse {

    Long folderId;
    Long parentId;
    int level;
    String name;


}
